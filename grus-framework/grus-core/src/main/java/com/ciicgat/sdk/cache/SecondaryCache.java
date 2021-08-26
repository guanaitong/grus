/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;

import com.ciicgat.sdk.lang.exception.CacheDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * 二级缓存的cache是用来存储key和一级缓存ID的对应关系的，
 * 而这个对应关系我们一般保持不变，所以二级缓存的cache可以有个LocalCache的实现
 * <p>
 * Created by August.Zhou on 2016/12/27 13:35.
 */
public class SecondaryCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryCache.class);

    private LocalCache cache;

    public SecondaryCache(LocalCache cache) {
        this.cache = cache;
    }


    /**
     * 获取关联缓存
     *
     * @param key               缓存key
     * @param firstValueLoader  主键缓存值加载，关联缓存找到才执行
     * @param secondValueLoader 关联缓存值加载，没找到关联缓存才执行
     * @return T 传递对象
     * @throws Exception
     */
    public <T> T get(Object key, final IntFunction<T> firstValueLoader, final Supplier<IdObject<T>> secondValueLoader) {
        // key 为空不能访问
        Objects.requireNonNull(key);


        T object = null;

        try {
            // 去找到访问关联缓存(保存的值基本就是主键ID)
            Integer primaryId = cache.getValue(key);
            // 是否找到关联缓存
            if (null != primaryId) {
                object = firstValueLoader.apply(primaryId);
                if (object != null) {
                    return object;
                }
            }

        } catch (CacheDataException e) {
            LOGGER.warn("获取一级缓存异常：", e);
        } catch (Throwable e) {
            LOGGER.error("根据主键查询异常：", e);
        }

        try {
            // 直接去数据库加载数据
            IdObject<T> io = secondValueLoader.get();
            if (null != io) {
                object = io.getObject();
                // 保存关联缓存
                cache.setValue(key, io.getId());
            }
        } catch (CacheDataException ex) {
            LOGGER.warn("保存二级缓存异常：", ex);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        return object;
    }


    public void invalidate(final Object key) {
        try {
            cache.evict(key);
        } catch (Exception ex) {
            LOGGER.warn(key.toString(), ex);
            throw new RuntimeException(ex);
        }
    }


}
