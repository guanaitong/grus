/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.sdk.gconf.BeanLoader;
import com.ciicgat.sdk.gconf.GConfBeanValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by August.Zhou
 */
abstract class ValueWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueWrapper.class);

    private static final Properties PROPERTIES_NULL = new Properties();
    private static final Map<String, Object> JSON_OBJECT_NULL = new HashMap<>();

    private static final Object BEAN_NULL = new Object();


    protected final String value;

    private Object asBeanByBeanLoaderCache;

    ValueWrapper(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }

    /**
     * could be Override
     *
     * @return
     */
    public Properties asProperties() {
        return PROPERTIES_NULL;
    }


    /**
     * could be Override
     *
     * @return
     */
    public Map<String, Object> asJSONObject() {
        return JSON_OBJECT_NULL;
    }

    /**
     * could be Override
     *
     * @param clazz
     * @return
     */
    public Object asBean(Class<?> clazz) {
        return null;
    }


    public final Object asBean(BeanLoader beanLoader) {
        if (asBeanByBeanLoaderCache == null) {
            Object v = beanLoader.load(value);
            validate(v);
            asBeanByBeanLoaderCache = v == null ? BEAN_NULL : v;
        }
        return asBeanByBeanLoaderCache == BEAN_NULL ? null : asBeanByBeanLoaderCache;
    }

    static void validate(Object v) {
        if (v instanceof GConfBeanValidator) {
            ((GConfBeanValidator) v).validate();
        }
    }

}
