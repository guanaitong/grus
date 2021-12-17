/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.secondary;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 二级缓存的cache是用来存储key和一级缓存ID的对应关系的，
 * 而这个对应关系我们一般保持不变，所以二级缓存的cache可以有个LocalCache的实现
 * <p>
 * Created by August.Zhou on 2016/12/27 13:35.
 */
public class SecondaryCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryCache.class);

    private Cache secondaryKeyToPrimaryIdCache;

    public SecondaryCache(Cache secondaryKeyToPrimaryIdCache) {
        this.secondaryKeyToPrimaryIdCache = secondaryKeyToPrimaryIdCache;
    }


    /**
     * 获取关联缓存
     *
     * @param secondaryKey              二级缓存key
     * @param valueLoaderByPrimaryId    主键缓存值加载，关联缓存找到才执行
     * @param valueLoaderBySecondaryKey 关联缓存值加载，没找到关联缓存才执行
     * @return T 传递对象
     * @throws Exception
     */
    public <ID, VALUE> VALUE get(Object secondaryKey, final Function<ID, VALUE> valueLoaderByPrimaryId, final Supplier<IdObject<ID, VALUE>> valueLoaderBySecondaryKey) {
        // key 为空不能访问
        Objects.requireNonNull(secondaryKey);
        ID id = null;
        try {
            Cache.ValueWrapper primaryIdValueWrapper = secondaryKeyToPrimaryIdCache.get(secondaryKey);
            if (Objects.nonNull(primaryIdValueWrapper)) {
                id = (ID) primaryIdValueWrapper.get();
            }
        } catch (RuntimeException e) {
            LOGGER.warn("getPrimaryIdBySecondaryKey error", e);
        }
        if (Objects.nonNull(id)) {
            // 去找到访问关联缓存(保存的值基本就是主键ID)
            // 是否找到关联缓存
            VALUE value = valueLoaderByPrimaryId.apply(id);
            if (Objects.nonNull(value)) {
                return value;
            }
            // 根据二级缓存能找到一级的ID，但是再根据一级ID却获取不到value，一般代表二级缓存一致性有问题。所以走下面的逻辑再从原始数据加载
        }

        IdObject<ID, VALUE> idObject = valueLoaderBySecondaryKey.get();
        if (Objects.nonNull(idObject) && Objects.nonNull(idObject.getId())) {
            try {
                // 直接去数据库加载数据
                // 保存关联缓存
                secondaryKeyToPrimaryIdCache.put(secondaryKey, idObject.getId());
            } catch (RuntimeException e) {
                LOGGER.warn("save secondaryKeyToPrimaryId error：", e);
            }
            return idObject.getObject();
        }
        return null;
    }


    public void invalidate(final Object secondaryKey) {
        try {
            secondaryKeyToPrimaryIdCache.evict(secondaryKey);
        } catch (Exception ex) {
            LOGGER.warn(secondaryKey.toString(), ex);
        }
    }


}
