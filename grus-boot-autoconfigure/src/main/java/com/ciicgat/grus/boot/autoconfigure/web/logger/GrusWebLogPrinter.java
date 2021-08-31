/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.grus.logger.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * @author Wei Jiaju
 * @author Stanley Shen
 */
@Aspect
public class GrusWebLogPrinter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusWebLogPrinter.class);

    private static final List<Class<?>> EXCLUDE_PARAM_TYPES = Arrays.asList(ServletRequest.class, ServletResponse.class, MultipartFile.class);

    private ConcurrentMap<Method, MethodPrinter> cache = new ConcurrentHashMap<>();

    private MethodPrinter getMethodPrinter(Signature signature, Method method) {
        MethodPrinter methodPrinter = cache.get(method);
        if (methodPrinter == null) {
            return cache.computeIfAbsent(method, method1 -> getMethodPrinter0(signature, method1));
        } else {
            return methodPrinter;
        }
    }

    private MethodPrinter getMethodPrinter0(Signature signature, Method method) {
        if ("isLive".equals(method.getName())) {
            return new MethodPrinter();
        }
        ResponseBody responseBody = findMergedAnnotation(method, ResponseBody.class);
        if (Objects.isNull(responseBody)) {
            return new MethodPrinter();
        }
        var clazz = signature.getDeclaringType();
        LogExclude logExclude = method.getAnnotation(LogExclude.class);
        if (Objects.isNull(logExclude)) {
            logExclude = AnnotationUtils.getAnnotation(clazz, LogExclude.class);
        }
        boolean logReq = true;
        boolean logResp = true;
        if (Objects.nonNull(logExclude)) {
            logReq = !logExclude.excludeReq();
            logResp = !logExclude.excludeResp();
        }
        if (logReq) {
            Parameter[] parameters = method.getParameters();
            List<Integer> indexList = new ArrayList<>(parameters.length);
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                LogExclude exclude = AnnotationUtils.getAnnotation(parameter, LogExclude.class);
                if (Objects.isNull(exclude)) {
                    for (Class<?> excludeParamType : EXCLUDE_PARAM_TYPES) {
                        if (excludeParamType.isAssignableFrom(parameter.getType())) {
                            indexList.add(Integer.valueOf(i));
                        }
                    }
                }
            }
            if (indexList.isEmpty()) {
                return new MethodPrinter(new int[0], false, logResp);
            } else {
                int[] requestLogParameterIndex = new int[indexList.size()];
                for (int i = 0; i < indexList.size(); i++) {
                    requestLogParameterIndex[i] = indexList.get(i).intValue();
                }
                return new MethodPrinter(requestLogParameterIndex, true, logResp);
            }
        }
        return new MethodPrinter(new int[0], false, logResp);
    }

    private static final class MethodPrinter {
        private final boolean log;
        private final int[] requestLogParameterIndex;
        private final boolean logReq;
        private final boolean logResp;

        MethodPrinter() {
            this(new int[0], false, false);
        }

        MethodPrinter(int[] requestLogParameterIndex, boolean logReq, boolean logResp) {
            this.log = logReq || logReq;
            this.requestLogParameterIndex = requestLogParameterIndex;
            this.logReq = logReq;
            this.logResp = logResp;
        }
    }


    @Around("execution(* *(..)) && (" +
            "within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Controller *) " +
            "))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        Method targetMethod = ((MethodSignature) signature).getMethod();

        Object[] params = joinPoint.getArgs();
        MethodPrinter methodPrinter = getMethodPrinter(signature, targetMethod);
        if (!methodPrinter.log) {
            // 不需要打印日志
            return joinPoint.proceed(params);
        }
        String methodName = signature.getDeclaringType().getSimpleName() + "." + targetMethod.getName();

        if (methodPrinter.logReq) {
            List<Object> toPrintParamsList = new ArrayList<>(methodPrinter.requestLogParameterIndex.length);
            for (int i = 0, len = methodPrinter.requestLogParameterIndex.length; i < len; i++) {
                toPrintParamsList.add(params[i]);
            }
            LOGGER.info("WEB_REQ METHOD: {} PARAM: {}", methodName, LogUtil.truncate(JSON.toJSONString(toPrintParamsList)));
        }
        try {
            Object resp = joinPoint.proceed(params);
            if (methodPrinter.logResp) {
                LOGGER.info("WEB_RSP METHOD: {} RESULT: {}", methodName, LogUtil.truncate(JSON.toJSONString(resp)));
            }
            return resp;
        } catch (Throwable e) {
            LOGGER.error(String.format("WEB_EX METHOD: %s ERROR", methodName), e);
            throw e;
        }
    }

}
