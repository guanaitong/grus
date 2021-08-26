/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.sdk.util.ComponentStatus;
import feign.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-05-24 15:40
 */
public class GrusFeignContractUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusFeignContractUtil.class);


    static {
        if (!ComponentStatus.isSpringMvcEnable()) {
            LOGGER.warn("not in spring-web, use default GrusContract. if you want to use feign extension, please import spring-web");
        }
    }

    public static Contract getFeignContract(Class<?> serviceClazz) {
        if (!ComponentStatus.isSpringMvcEnable()) {
            return new GrusContract();
        }

        Method[] declaredMethods = serviceClazz.getDeclaredMethods();

        int useRequestMapping = 0;
        for (Method declaredMethod : declaredMethods) {
            RequestMapping methodRequestMapping = AnnotationUtils.getAnnotation(declaredMethod, RequestMapping.class);
            if (methodRequestMapping != null) {
                useRequestMapping++;
            }
        }
        if (useRequestMapping == declaredMethods.length) {
            return new GrusMvcContract();
        } else if (useRequestMapping == 0) {
            return new GrusContract();
        } else {
            // 不允许在类中交叉使用两种contract
            throw new RuntimeException("SYS_ERR. Using different contract:" + serviceClazz.getName());
        }
    }

}
