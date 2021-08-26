/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.sdk.gconf.annotation.BeanFieldKey;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by August.Zhou on 2020-04-26 10:45.
 */
class PropertiesValueWrapper extends ValueWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesValueWrapper.class);

    public static final String UNDERLINE = "_|-";
    private final Properties properties;
    private Object asBeanCache;


    PropertiesValueWrapper(String value, Properties properties) {
        super(value);
        this.properties = properties;
    }


    @Override
    public Properties asProperties() {
        return this.properties;
    }

    @Override
    public Object asBean(Class<?> clazz) {
        if (asBeanCache == null) {
            Object v = newBean(clazz);
            validate(v);
            this.asBeanCache = v;
        }
        return asBeanCache;
    }

    private Object newBean(Class<?> clazz) {
        Object object;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.error("init bean failed, class " + clazz.getName(), e);
            return null;
        }
        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            String mapKey = fieldName;
            try {
                field.setAccessible(true);
                BeanFieldKey beanFieldKey = field.getAnnotation(BeanFieldKey.class);
                if (beanFieldKey != null && !beanFieldKey.value().isBlank()) {
                    mapKey = beanFieldKey.value();
                }
                String fieldValue = getMapValue(mapKey);
                if (fieldValue == null || "".equals(fieldValue)) {
                    continue;
                }
                Class<?> fieldClazz = field.getType();
                field.set(object, toObject(fieldClazz, fieldValue));
            } catch (Exception e) {
                LOGGER.error("fieldName:" + fieldName + " clazz:" + clazz, e);
            }
        }
        return object;
    }

    private String getMapValue(String key) {
        Object value = properties.get(key);
        if (value != null) {
            return value.toString();
        }
        String formatKey = format(key);
        for (Map.Entry<?, Object> objectEntry : properties.entrySet()) {
            String fk = format(objectEntry.getKey().toString());
            if (formatKey.equals(fk)) {
                if (objectEntry.getValue() != null) {
                    return objectEntry.getValue().toString();
                } else return null;
            }
        }
        return null;
    }

    private static String format(String text) {
        return text.toLowerCase().replaceAll(UNDERLINE, "");
    }

    private static Object toObject(Class<?> clazz, String value) {
        if (String.class.equals(clazz)) {
            return value;
        } else if (Byte.TYPE.equals(clazz) || Byte.class.equals(clazz)) {
            return Byte.valueOf(value);
        } else if (Short.TYPE.equals(clazz) || Short.class.equals(clazz)) {
            return Short.valueOf(value);
        } else if (Integer.TYPE.equals(clazz) || Integer.class.equals(clazz)) {
            return Integer.valueOf(value);
        } else if (Long.TYPE.equals(clazz) || Long.class.equals(clazz)) {
            return Long.valueOf(value);
        } else if (BigDecimal.class.equals(clazz)) {
            return new BigDecimal(value);
        } else if (Double.TYPE.equals(clazz) || Double.class.equals(clazz)) {
            return Double.valueOf(value);
        } else if (Float.TYPE.equals(clazz) || Float.class.equals(clazz)) {
            return Float.valueOf(value);
        } else if (Boolean.TYPE.equals(clazz) || Boolean.class.equals(clazz)) {
            return Boolean.parseBoolean(value);
        } else if (Date.class.equals(clazz)) {
            try {
                return new Date(Long.parseLong(value));
            } catch (NumberFormatException e) {
                try {
                    return DateUtils.parseDate(value, new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"});
                } catch (ParseException parseException) {
                    LOGGER.warn("parse date failed,value: " + value, parseException);
                }
            }
        }
        RuntimeException exception = new RuntimeException(clazz.toString() + "  " + value);
        throw exception;
    }

}
