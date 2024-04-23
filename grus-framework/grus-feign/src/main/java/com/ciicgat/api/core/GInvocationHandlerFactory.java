/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ciicgat.api.core.annotation.ApiCache;
import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.grus.logger.LogUtil;
import com.ciicgat.grus.sentinel.Resources;
import com.ciicgat.grus.sentinel.SentinelDefaultConfig;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeConfig;
import com.ciicgat.grus.service.GrusService;
import com.ciicgat.grus.service.GrusServiceStatus;
import com.ciicgat.grus.service.Services;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.annotations.VisibleForTesting;
import feign.InvocationHandlerFactory;
import feign.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static feign.Util.checkNotNull;

/**
 * java方法调用的代理工厂及代理类
 *
 * @author Wei Jiaju
 * @date Created in 2018/6/19
 */
@VisibleForTesting
class GInvocationHandlerFactory implements InvocationHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(GInvocationHandlerFactory.class);

    private static final int HTTP_STATUS_TOO_MANY_REQUESTS = 429;

    private static Object NULL = new Object();

    private final String serviceName;
    private final GrusService grusService;
    private final GrusServiceStatus grusServiceStatus;
    private final CacheOptions cacheOptions;
    private final FallbackFactory<?> fallbackFactory;

    private final List<String> ignoreErrorMethods;

    private GrusRuntimeConfig grusRuntimeConfig = GrusFramework.getGrusRuntimeManager().getGrusRuntimeConfig();

    private final boolean logReq;

    private final boolean logResp;


    GInvocationHandlerFactory(GrusServiceStatus grusServiceStatus,
                              CacheOptions cacheOptions,
                              FallbackFactory<?> fallbackFactory,
                              boolean logReq,
                              boolean logResp,
                              List<String> ignoreErrorMethods) {
        this.serviceName = Objects.requireNonNull(grusServiceStatus.getGrusService().getServiceName());
        this.cacheOptions = cacheOptions;
        this.grusServiceStatus = grusServiceStatus;
        this.grusService = grusServiceStatus.getGrusService();
        this.fallbackFactory = fallbackFactory;
        this.logReq = logReq;
        this.logResp = logResp;
        this.ignoreErrorMethods = Collections.unmodifiableList(ignoreErrorMethods);
    }

    static Map<Method, Method> toFallbackMethod(Map<Method, MethodHandler> dispatch) {
        Map<Method, Method> result = new LinkedHashMap<>(dispatch.size());
        for (Method method : dispatch.keySet()) {
            method.setAccessible(true);
            result.put(method, method);
        }
        return result;
    }

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return new GFeignInvocationHandler(target, dispatch);
    }


    private class GFeignInvocationHandler implements InvocationHandler {
        private final Target target;
        private final Map<Method, MethodMeta> methodMetaMap = new HashMap<>();


        GFeignInvocationHandler(Target target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatcher) {
            this.target = checkNotNull(target, "target");
            checkNotNull(dispatcher, "dispatch for %s", target);
            for (Map.Entry<Method, MethodHandler> entry : dispatcher.entrySet()) {
                Method method = entry.getKey();
                LogExclude logExclude = Services.getLogExclude(method);
                String resourceName = Resources.buildMethodResource(serviceName, method);
                MethodMeta methodMeta = new MethodMeta(method, entry.getValue(), getApiCache(method), resourceName,
                        logReq && (logExclude == null || !logExclude.excludeReq()),
                        logResp && (logExclude == null || !logExclude.excludeResp()),
                        ignoreErrorMethods.contains(method.getName()));
                methodMetaMap.put(method, methodMeta);
            }
        }

        private ApiCache getApiCache(Method method) {
            if (cacheOptions == null) {
                return method.getAnnotation(ApiCache.class);
            }
            if (method.getName().equals(cacheOptions.getMethod())) {
                return cacheOptions;
            }
            return null;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) :
                            null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return Boolean.FALSE;
                }
            } else if ("hashCode".equals(method.getName())) {
                return super.hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            }
            MethodMeta methodMeta = methodMetaMap.get(method);
            // 默认方法
            if (methodMeta.isDefault()) {
                return methodMeta.getMethodHandler().invoke(args);
            }
            try {
                if (methodMeta.isLogReq()) {
                    LOGGER.info("FEIGN_REQ METHOD: {} PARAM: {}", getPrintMethod(method), args == null ? "" :
                            LogUtil.truncate(JSON.toJSONString(args)));
                }
                Object resp;
                if (methodMeta.getApiCache() != null) {
                    resp = invokeWithCache(methodMeta, args);
                } else {
                    resp = invokeWithoutCache(methodMeta, args);
                }
                if (methodMeta.isLogResp()) {
                    LOGGER.info("FEIGN_RSP METHOD: {} RESULT: {}", getPrintMethod(method), resp == null ? "" :
                            LogUtil.truncate(JSON.toJSONString(resp)));
                }
                return resp;
            } catch (BusinessFeignException throwable) {
                throw throwable;
            } catch (BusinessRuntimeException throwable) {
                // 如果是有定义fallback且抛出的是熔断异常，则执行fallback；反之继续抛出异常
                if (ErrorCode.REQUEST_BLOCK.getErrorCode() != throwable.getErrorCode() || fallbackFactory == null) {
                    throw throwable;
                }
                Object fallback = fallbackFactory.create(throwable);
                return method.invoke(fallback, args);
            } catch (Throwable throwable) {
                if (methodMeta.isIgnoreError()) {
                    LOGGER.warn("Exception has been ignored", throwable);
                    return methodMeta.getDefaultReturnValue();
                }
                if (fallbackFactory == null) {
                    throw throwable;
                }
                Object fallback = fallbackFactory.create(throwable);
                return method.invoke(fallback, args);
            }
        }

        private Object invokeWithoutCache(MethodMeta methodMeta, Object[] args) throws Throwable {
            Method method = methodMeta.getMethod();
            String resourceName = methodMeta.getResourceName();
            Entry entry = null;
            // sentinel处理
            try {
                ContextUtil.enter(SentinelDefaultConfig.SENTINEL_FEIGN_CONTEXT_NAME);
                entry = SphU.entry(resourceName, EntryType.OUT, 1);
                // 注入调用方appName，需要在SphU.entry方法之前执行才生效，并且需要放到try代码块，故无法使用try resources写法
                return methodMeta.getMethodHandler().invoke(args);
            } catch (BlockException e) {
                LOGGER.error("clientside blocked, resourceName {} rule {} ", resourceName, e.getRule(), e);
                Alert.send(String.format("clientside blocked, resourceName %s rule %s ", resourceName, e.getRule()));
                throw new BusinessRuntimeException(ErrorCode.REQUEST_BLOCK, null, HTTP_STATUS_TOO_MANY_REQUESTS);
            } catch (Throwable ex) {
                if (!(ex instanceof BusinessFeignException)) {
                    LOGGER.error("error", ex);
                }
                // 降级跟踪必备代码
                Tracer.trace(ex);
                throw ex;
            } finally {
                if (entry != null) {
                    entry.exit(1);
                }
                ContextUtil.exit();
            }
        }


        /**
         * 处理本地缓存
         *
         * @param args args
         * @return 实际对象
         * @throws Throwable
         */
        private Object invokeWithCache(MethodMeta methodMeta, Object[] args) throws Throwable {
            Method method = methodMeta.getMethod();
            final ApiCache apiCache = methodMeta.getApiCache();
            final Cache<String, Object> cache = methodMeta.getCache();

            //拼接缓存key
            final StringBuilder sb = new StringBuilder(method.getName());
            for (int seq : apiCache.params()) {
                sb.append(args[seq]);
                sb.append("__");
            }
            final String cacheKey = sb.toString();
            //获取缓存，如空则加载
            Object cacheValue = cache.getIfPresent(cacheKey);
            if (cacheValue != null) {
                return cacheValue == NULL ? null : cacheValue;
            }
            Object result = invokeWithoutCache(methodMeta, args);
            //GUAVA CACHE的value是不能为null的，不然会抛异常
            if (result != null) {
                cache.put(cacheKey, result);
            } else if (apiCache.cacheNullValue()) {
                cache.put(cacheKey, NULL);
            }
            return result;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof GFeignInvocationHandler) {
                GFeignInvocationHandler other = (GFeignInvocationHandler) obj;
                return target.equals(other.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return target.toString();
        }
    }

    private static String getPrintMethod(Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }
}

