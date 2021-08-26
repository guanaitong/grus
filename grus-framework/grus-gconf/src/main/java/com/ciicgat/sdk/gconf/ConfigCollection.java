/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;


import java.util.Map;
import java.util.Properties;

/**
 * Created by August.Zhou on 2016/10/11 10:50.
 */
public interface ConfigCollection {

    /**
     * 获取当前配置集合的信息
     *
     * @return
     */
    ConfigApp getConfigCollectionInfo();


    /**
     * 获取配置值
     *
     * @param key
     * @return
     */
    String getConfig(String key);

    /**
     * 当配置文件为properties格式的时候，会返回一个填充后的Properties对象，否则返回一个空对象
     *
     * @param key
     * @return
     */
    Properties getProperties(String key);

    /**
     * 当配置文件为json格式的时候，会返回一个填充后的Map对象，否则返回一个空对象
     *
     * @param key
     * @return
     */
    Map<String, Object> getJSONObject(String key);

    /**
     * 返回一个会自动更新的bean。
     * 该bean会被gconf管理。当有数据变更时，bean里的属性会被gconf更新
     * <p>
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * @memo 不要使用多个不同的实现类去接收配置对象，后取的对象将无法正常使用到缓存
     */
    <T> T getBean(String key, Class<T> clazz);

    /**
     * 根据当前的数据，实时返回一个最新的的bean。该bean不可以修改
     * <p>
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * @memo 不要使用多个不同的实现类去接收配置对象，后取的对象将无法正常使用到缓存
     */
    <T> T getLatestBean(String key, Class<T> clazz);

    /**
     * 根据用户自己的beanLoader生成bean，返回的bean里的属性不会自动刷新
     *
     * @param key
     * @param beanLoader
     * @param <T>
     * @return
     */
    <T> T getBean(String key, BeanLoader<T> beanLoader);

    /**
     * 在一个key下，注册一个listener。只有该key下的配置变更，才会触发监听事件
     *
     * @param key
     * @param configChangeListener
     */
    void addConfigChangeListener(String key, ConfigChangeListener configChangeListener);

    /**
     * 注册一个全局的listener。只要该configApp下有配置变更，都会触发监听事件
     *
     * @param configChangeListener
     */
    void addGlobalConfigChangeListener(ConfigChangeListener configChangeListener);

    /**
     * 取出配置集合里所有的配置信息
     *
     * @return
     */
    Map<String, String> asMap();

}
