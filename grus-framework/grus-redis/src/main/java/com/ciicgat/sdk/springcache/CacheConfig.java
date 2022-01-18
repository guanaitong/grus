/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import com.ciicgat.sdk.springcache.refresh.RefreshPolicy;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Cache 配置参数
 *
 * @author wanchongyang
 * @date 2021/1/15 11:05 下午
 */
public abstract class CacheConfig<C extends CacheConfig<C>> {


    /**
     * 是否cacheNull值。
     * 非NULL时，优先级最高
     */
    private Boolean cacheNull;

    /**
     * 使用gzip压缩存储缓存数据，缓存数据大时建议开启
     * 非NULL时，优先级最高
     */
    private Boolean useGzip;

    /**
     * 序列化
     * 非NULL时，优先级最高
     */
    private RedisSerializer<Object> serializer;

    /**
     * 单挑缓存的异步刷新策略，非NULL时，优先级高于全局
     */
    private RefreshPolicy refreshPolicy;


    public Boolean getCacheNull() {
        return cacheNull;
    }

    public C setCacheNull(Boolean cacheNull) {
        this.cacheNull = cacheNull;
        return (C) this;
    }

    public Boolean getUseGzip() {
        return useGzip;
    }


    public C setUseGzip(Boolean useGzip) {
        this.useGzip = useGzip;
        return (C) this;
    }

    public RedisSerializer<Object> getSerializer() {
        return serializer;
    }

    public C setSerializer(RedisSerializer<Object> serializer) {
        this.serializer = serializer;
        return (C) this;
    }

    public RefreshPolicy getRefreshPolicy() {
        return refreshPolicy;
    }

    public C setRefreshPolicy(RefreshPolicy refreshPolicy) {
        this.refreshPolicy = refreshPolicy;
        return (C) this;
    }

    private static final int DEFAULT_LOCAL_INIT_SIZE = 128;
    private static final int DEFAULT_LOCAL_EXPIRE_SECONDS = 3600;
    private static final long DEFAULT_LOCAL_MAX_SIZE = 40960L;

    /**
     * 本地缓存
     * @return
     */
    public static Local local() {
        return new Local();
    }

    /**
     * redis缓存
     * @return
     */
    public static Redis<? extends Redis> redis() {
        return new Redis<>();
    }

    /**
     * 二级缓存（本地+redis）
     * @return
     */
    public static L2 l2() {
        return new L2();
    }

    public abstract AbstractCache newCache(String name, RedisCacheManager redisCacheManager);

    /**
     * Local specific cache properties.
     */
    public static class Local extends CacheConfig<Local> {
        /**
         * 是否序列化value，如果为否，那么缓存的value为对象，如果是，那么缓存的value为对象序列化后的字节数组
         * 不使用序列化性能更加高。但是需要保证返回的值，不能做修改。
         */
        private boolean serialize;

        /**
         * 缓存最大值
         */
        private long maximumSize = DEFAULT_LOCAL_MAX_SIZE;

        /**
         * 过期时间，单位s
         */
        private int expireSeconds = DEFAULT_LOCAL_EXPIRE_SECONDS;

        /**
         * 初始化大小
         */
        private int initialCapacity = DEFAULT_LOCAL_INIT_SIZE;

        Local() {
        }

        public boolean isSerialize() {
            return serialize;
        }

        public Local setSerialize(boolean serialize) {
            this.serialize = serialize;
            return this;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public Local setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public int getExpireSeconds() {
            return expireSeconds;
        }

        public Local setExpireSeconds(int expireSeconds) {
            this.expireSeconds = expireSeconds;
            return this;
        }

        public int getInitialCapacity() {
            return initialCapacity;
        }

        public Local setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        @Override
        public AbstractCache newCache(String name, RedisCacheManager redisCacheManager) {
            return new LocalCache(name, redisCacheManager, this);
        }
    }

    /**
     * Redis-specific cache properties.
     */
    public static class Redis<C extends Redis<C>> extends CacheConfig<C> {

        /**
         * 过期时间，单位s
         */
        private int expireSeconds = 3600;

        Redis() {
        }

        public int getExpireSeconds() {
            return expireSeconds;
        }

        public C setExpireSeconds(int expireSeconds) {
            this.expireSeconds = expireSeconds;
            return (C) this;
        }

        @Override
        public AbstractCache newCache(String name, RedisCacheManager redisCacheManager) {
            return new RedisCache(name, redisCacheManager, this);
        }
    }

    /**
     * LocalRedis-specific cache properties.
     */
    public static class L2 extends Redis<L2> {
        /**
         * 是否序列化value，如果为否，那么缓存的value为对象，如果是，那么缓存的value为对象序列化后的字节数组
         * 不使用序列化性能更加高。但是需要保证返回的值，不能做修改。
         */
        private boolean serialize;

        /**
         * 缓存最大值
         */
        private long maximumSize = DEFAULT_LOCAL_MAX_SIZE;

        /**
         * local cache过期时间，单位s
         */
        private int localExpireSeconds = DEFAULT_LOCAL_EXPIRE_SECONDS;

        /**
         * 初始化大小
         */
        private int initialCapacity = DEFAULT_LOCAL_INIT_SIZE;

        L2() {
        }

        public boolean isSerialize() {
            return serialize;
        }

        public L2 setSerialize(boolean serialize) {
            this.serialize = serialize;
            return this;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public L2 setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public int getLocalExpireSeconds() {
            return localExpireSeconds;
        }

        public L2 setLocalExpireSeconds(int localExpireSeconds) {
            this.localExpireSeconds = localExpireSeconds;
            return this;
        }


        public int getInitialCapacity() {
            return initialCapacity;
        }

        public L2 setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        @Override
        public AbstractCache newCache(String name, RedisCacheManager redisCacheManager) {
            return new L2Cache(name, redisCacheManager, this);
        }
    }
}
