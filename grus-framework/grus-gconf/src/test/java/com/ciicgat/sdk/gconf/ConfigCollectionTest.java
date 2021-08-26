/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by August.Zhou on 2018/2/7 17:16.
 */
public class ConfigCollectionTest {

    ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("grus-test");

    @Test
    public void testApp() {
        Assert.assertNotNull(configCollection.getConfigCollectionInfo());
        Assert.assertTrue(!configCollection.asMap().isEmpty());
        Assert.assertNull(RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection("12345678lskdfjj"));
    }

    @Test
    public void testProperties() {
        String key = "session-prop.properties";
        Assert.assertNotNull(configCollection.getConfig(key));
        SessionProp bean = configCollection.getBean(key, SessionProp.class);
        SessionProp bean2 = configCollection.getBean(key, SessionProp.class);
        Assert.assertSame(bean, bean2);
        Properties properties = configCollection.getProperties(key);
        Assert.assertEquals(properties.get("default_session_alive_time"), "3600");
        Assert.assertEquals(properties.get("test"), "123");

        Assert.assertTrue(configCollection.getJSONObject(key).size() == 0);

        SessionProp bean11 = configCollection.getLatestBean(key, SessionProp.class);
        SessionProp bean22 = configCollection.getLatestBean(key, SessionProp.class);
        Assert.assertSame(bean, bean11);
        Assert.assertSame(bean11, bean22);
    }

    @Test
    public void testJson() {
        String key = "session-redis-config.json";
        Assert.assertNotNull(configCollection.getConfig(key));
        RedisSettingT bean1 = configCollection.getBean(key, RedisSettingT.class);
        RedisSettingT bean2 = configCollection.getBean(key, RedisSettingT.class);
        Assert.assertSame(bean1, bean2);
        Assert.assertEquals(bean1.getDb(), 4);
        Assert.assertEquals(bean1.getStandalone().getHost(), "redis.servers.dev.ofc");

        Map<String, Object> jsonObject = configCollection.getJSONObject(key);
        Assert.assertEquals(jsonObject.get("db"), 4);
        Object standalone = jsonObject.get("standalone");
        Assert.assertEquals(standalone.getClass(), LinkedHashMap.class);
        LinkedHashMap<String, Object> standaloneMap = (LinkedHashMap) standalone;
        Assert.assertEquals(standaloneMap.get("host"), "redis.servers.dev.ofc");


        Assert.assertTrue(configCollection.getProperties(key).size() == 0);

        RedisSettingT bean3 = configCollection.getBean(key, content -> JSON.parse(content, RedisSettingT.class));
        RedisSettingT bean4 = configCollection.getBean(key, content -> JSON.parse(content, RedisSettingT.class));
        Assert.assertSame(bean3, bean4);
    }

    @Test
    public void testDefault() {
        String key = "xxx";
        Assert.assertEquals(configCollection.getConfig(key), "yyy");
        Assert.assertNull(configCollection.getBean(key, SessionProp.class));
    }

    @Test
    public void testNotExist() {
        String keyNotExist = "session-prop-not-exist.properties";
        Assert.assertNull(configCollection.getBean(keyNotExist, SessionProp.class));
        Assert.assertNull(configCollection.getLatestBean(keyNotExist, SessionProp.class));
        Assert.assertNull(configCollection.getBean(keyNotExist, content -> JSON.parse(content, SessionProp.class)));
        Assert.assertNull(configCollection.getJSONObject(keyNotExist));
        Assert.assertNull(configCollection.getProperties(keyNotExist));
        Assert.assertNull(configCollection.getConfig(keyNotExist));
    }

    @Test
    public void testGConfBeanValidator() {
        RedisPropClazz bean = configCollection.getBean("session-redis.properties", RedisPropClazz.class);
        RedisPropClazz bean2 = configCollection.getBean("session-redis.properties", RedisPropClazz.class);

        Assert.assertSame(bean, bean2);
        Assert.assertEquals(Integer.valueOf(1), bean.getNotBean());
    }

    @Test
    public void testListener() {
        configCollection.addGlobalConfigChangeListener(new ConfigChangeListener() {
            @Override
            public void valueChanged(String key, String oldValue, String newValue) {

            }
        });

        configCollection.addConfigChangeListener("session-redis.properties", new ConfigChangeListener() {
            @Override
            public void valueChanged(String key, String oldValue, String newValue) {

            }
        });
    }

}
