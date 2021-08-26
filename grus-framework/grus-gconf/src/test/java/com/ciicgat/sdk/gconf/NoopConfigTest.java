/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.noop.NoopConfigCollectionFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by August.Zhou on 2020-04-26 14:28.
 */
public class NoopConfigTest {

    ConfigCollectionFactory configCollectionFactory = new NoopConfigCollectionFactory();

    @Test
    public void test() {
        ConfigCollection configCollection = configCollectionFactory.getConfigCollection();
        ConfigCollection configCollection1 = configCollectionFactory.getConfigCollection("1231231");
        ConfigCollection configCollection2 = configCollectionFactory.getConfigCollection("abc");
        ConfigCollection configCollection3 = configCollectionFactory.getConfigCollection();
        Assert.assertSame(configCollection, configCollection1);
        Assert.assertSame(configCollection, configCollection2);
        Assert.assertSame(configCollection, configCollection3);

        Assert.assertSame(configCollection.getConfigCollectionInfo(), ConfigApp.NOOP_INSTANCE);
        Properties properties = configCollection.getProperties("123");
        Properties properties1 = configCollection.getProperties("abc");
        Assert.assertTrue(properties.isEmpty());
        Assert.assertSame(properties, properties1);

        Assert.assertSame(configCollection.getJSONObject("123"), configCollection.getJSONObject("abc"));


        Assert.assertTrue(configCollection.getConfig("123aaa").isEmpty());
        Assert.assertTrue(configCollection.getProperties("123aaxxxa").isEmpty());
        Assert.assertTrue(configCollection.getJSONObject("123aaaccc").isEmpty());
        Assert.assertTrue(configCollection.asMap().isEmpty());
        Assert.assertNull(configCollection.getBean("123ccc", RedisSettingT.class));
        Assert.assertNull(configCollection.getBean("123ccc", content -> JSON.parse(content, RedisSettingT.class)));
        Assert.assertNull(configCollection.getLatestBean("123bbb", RedisSettingT.class));

        configCollection.addConfigChangeListener("xkljhsadf", new ConfigChangeListener() {
            @Override
            public void valueChanged(String key, String oldValue, String newValue) {

            }
        });

        configCollection.addGlobalConfigChangeListener(new ConfigChangeListener() {
            @Override
            public void valueChanged(String key, String oldValue, String newValue) {

            }
        });
    }
}
