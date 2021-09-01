/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.sdk.gconf.BeanLoader;
import com.ciicgat.sdk.gconf.ConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 当key对应的value有变化时，在DataStore里，key对应的ValueReference对象不会变化，而ValueReference里的valueWrapper对象会刷新
 * <p>
 * Created by August.Zhou on 2017/1/20 18:02.
 */
@SuppressWarnings("unchecked")
class ValueReference {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueReference.class);
    private final String key;

    private volatile ValueWrapper valueWrapper;

    private volatile Object proxyBean;

    private CopyOnWriteArrayList<ConfigChangeListener> configChangeListeners = new CopyOnWriteArrayList<>();

    ValueReference(String key, String raw) {
        this.key = key;
        this.setRaw(raw);
    }

    /**
     * 初始化时和value变化时，会调用此方法
     *
     * @param raw
     */
    public void setRaw(String raw) {
        this.valueWrapper = ValueWrappers.of(this.key, raw);
        if (proxyBean != null) {
            try {
                //先生成一个全新的object
                Object newProxyBean = valueWrapper.asBean(proxyBean.getClass());
                updateProxyBeanByNewValue(newProxyBean);
            } catch (Exception e) {
                LOGGER.error("refresh proxyBean failed", e);
            }

        }
    }

    private void updateProxyBeanByNewValue(Object newProxyBean) {
        Class<?> clazz = this.proxyBean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            try {
                field.setAccessible(true);
                Object newFieldValue = field.get(newProxyBean);
                field.set(this.proxyBean, newFieldValue);
            } catch (Exception e) {
                LOGGER.error("fieldName:" + fieldName + " clazz:" + clazz, e);
            }
        }
    }


    public String getRaw() {
        return valueWrapper.getValue();
    }

    public Properties asProperties() {
        return valueWrapper.asProperties();
    }

    public Map<String, Object> asJSONObject() {
        return valueWrapper.asJSONObject();
    }

    public <T> T asBean(Class<T> tClass) {
        if (proxyBean == null) {
            synchronized (this) {
                if (proxyBean == null) {
                    proxyBean = valueWrapper.asBean(tClass);
                }
            }
        }
        return (T) proxyBean;
    }


    public <T> T asBean(BeanLoader<T> beanLoader) {
        return (T) valueWrapper.asBean(beanLoader);
    }

    public <T> T asLatestBean(final Class<T> clazz) {
        return (T) valueWrapper.asBean(clazz);
    }

    public void addConfigChangeListener(ConfigChangeListener configChangeListener) {
        this.configChangeListeners.add(configChangeListener);
    }

    public void fireValueChanged(String oldValue, String newValue) {
        for (ConfigChangeListener configChangeListener : configChangeListeners) {
            try {
                configChangeListener.valueChanged(key, oldValue, newValue);
            } catch (Exception e) {
                LOGGER.error("fireValueChanged error", e);
            }
        }
    }

    @Override
    public String toString() {
        return "ValueReference{" +
                "key='" + key + '\'' +
                ", raw='" + this.getRaw() + '\'' +
                '}';
    }

}
