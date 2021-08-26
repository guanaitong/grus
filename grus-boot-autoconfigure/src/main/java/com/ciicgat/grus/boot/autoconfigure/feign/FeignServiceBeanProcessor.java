/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import com.ciicgat.api.core.CacheOptions;
import com.ciicgat.api.core.FallbackFactory;
import com.ciicgat.api.core.FeignServiceBuilder;
import com.ciicgat.grus.service.naming.NamingService;
import feign.Request;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Modifier;

/**
 * Created by August.Zhou on 2019-03-05 13:38.
 */
public class FeignServiceBeanProcessor implements BeanPostProcessor {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private FeignProperties feignProperties;

    @Autowired
    private NamingService namingService;


    public FeignServiceBeanProcessor() {
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> beanClass = bean.getClass();

        ReflectionUtils.doWithFields(beanClass, field -> {
            FeignService feignServiceAnnotation = field.getAnnotation(FeignService.class);

            if (feignServiceAnnotation == null) {
                return;
            }

            Class<?> interfaceType = feignServiceAnnotation.interfaceType();
            if (interfaceType.equals(void.class)) {
                interfaceType = field.getType();
            }

            Object proxy = createReferenceProxy(feignServiceAnnotation, interfaceType);
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, bean, proxy);
        }, field -> !Modifier.isStatic(field.getModifiers())
                && field.isAnnotationPresent(FeignService.class));

        ReflectionUtils.doWithMethods(beanClass, method -> {

            Class[] parameterTypes = method.getParameterTypes();
            Assert.isTrue(parameterTypes.length == 1,
                    "method should have one and only one parameter.");

            FeignService feignServiceAnnotation = method.getAnnotation(FeignService.class);
            if (feignServiceAnnotation == null) {
                return;
            }
            Class<?> interfaceType = feignServiceAnnotation.interfaceType();
            if (interfaceType.equals(void.class)) {
                interfaceType = parameterTypes[0];
            }

            Object proxy = createReferenceProxy(feignServiceAnnotation, interfaceType);
            ReflectionUtils.invokeMethod(method, bean, proxy);
        }, method -> method.isAnnotationPresent(FeignService.class));

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    private Object createReferenceProxy(FeignService feignServiceAnnotation,
                                        Class<?> interfaceType) {
        TimeoutBinding timeoutBinding = feignServiceAnnotation.timeoutBinding();
        CacheBinding cacheBinding = feignServiceAnnotation.cacheBinding();


        FeignServiceBuilder feignServiceBuilder = FeignServiceBuilder.newBuilder();

        feignServiceBuilder
                .serviceClazz(interfaceType)
                .fromCache(true)
                .logReq(feignProperties.isLogReq())
                .logResp(feignProperties.isLogResp())
                .namingService(namingService)
                .options(new Request.Options(timeoutBinding.connectTimeoutMillis(), timeoutBinding.readTimeoutMillis()));

        if (feignServiceAnnotation.fallback() != void.class) {
            if (interfaceType.isAssignableFrom(feignServiceAnnotation.fallback())) {
                try {

                    Object fallback = feignServiceAnnotation.fallback().getDeclaredConstructor().newInstance();
                    feignServiceBuilder.fallbackFactory(new FallbackFactory.Default<>(fallback));
                    feignServiceBuilder.fromCache(false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("fallback指定的类，必须是服务interface的实现类");
            }

        } else if (feignServiceAnnotation.fallbackFactory() != FallbackFactory.class) {
            try {
                FallbackFactory fallbackFactory = feignServiceAnnotation.fallbackFactory().getDeclaredConstructor().newInstance();
                feignServiceBuilder.fallbackFactory(fallbackFactory);
                feignServiceBuilder.fromCache(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }


        if (StringUtils.hasLength(cacheBinding.method())) {
            ReflectionUtils.doWithMethods(interfaceType, method -> {
                CacheOptions cacheOptions = new CacheOptions()
                        .setMethod(cacheBinding.method())
                        .setParams(cacheBinding.params())
                        .setExpireSeconds(cacheBinding.expireSeconds())
                        .setConcurrencyLevel(cacheBinding.concurrencyLevel())
                        .setCacheNullValue(cacheBinding.cacheNullValue())
                        .setMaxCacheSize(cacheBinding.maxCacheSize());

                feignServiceBuilder.cacheOptions(cacheOptions);
            }, method -> cacheBinding.method().equals(method.getName()));
        }

        Object feignProxy = feignServiceBuilder.build();
//        registerReferenceProxy(interfaceType, feignProxy);
        return feignProxy;
    }

    private void registerReferenceProxy(Class<?> interfaceType, Object proxy) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        String name = "feignService__" + interfaceType.getSimpleName() + "__" + proxy.hashCode();
        if (beanFactory.containsBean(name)) {
            return;
        }
        beanFactory.registerSingleton(name, proxy);
    }


}
