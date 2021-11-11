/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

/**
 * Cache 配置参数
 *
 * @author wanchongyang
 * @date 2021/1/15 11:05 下午
 */
public abstract class CacheConfig<CONFIG extends CacheConfig> {


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

    public Boolean getCacheNull() {
        return cacheNull;
    }

    public CONFIG setCacheNull(Boolean cacheNull) {
        this.cacheNull = cacheNull;
        return (CONFIG) this;
    }

    public Boolean getUseGzip() {
        return useGzip;
    }

    public CONFIG setUseGzip(Boolean useGzip) {
        this.useGzip = useGzip;
        return (CONFIG) this;
    }

    private static final int DEFAULT_LOCAL_INIT_SIZE = 128;
    private static final int DEFAULT_LOCAL_EXPIRE_SECONDS = 3600;
    private static final long DEFAULT_LOCAL_MAX_SIZE = 40960L;

    public static Local local() {
        return new Local();
    }

    public static Redis<Redis> redis() {
        return new Redis<>();
    }

    public static LocalRedis localRedis() {
        return new LocalRedis();
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
    public static class Redis<R extends Redis> extends CacheConfig<R> {

        /**
         * 过期时间，单位s
         */
        private int expireSeconds = 3600;

        Redis() {
        }

        public int getExpireSeconds() {
            return expireSeconds;
        }

        public R setExpireSeconds(int expireSeconds) {
            this.expireSeconds = expireSeconds;
            return (R) this;
        }

        @Override
        public AbstractCache newCache(String name, RedisCacheManager redisCacheManager) {
            return new RedisCache(name, redisCacheManager, this);
        }
    }

    /**
     * LocalRedis-specific cache properties.
     */
    public static class LocalRedis extends Redis<LocalRedis> {
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

        LocalRedis() {
        }

        public boolean isSerialize() {
            return serialize;
        }

        public LocalRedis setSerialize(boolean serialize) {
            this.serialize = serialize;
            return this;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public LocalRedis setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public int getLocalExpireSeconds() {
            return localExpireSeconds;
        }

        public LocalRedis setLocalExpireSeconds(int localExpireSeconds) {
            this.localExpireSeconds = localExpireSeconds;
            return this;
        }


        public int getInitialCapacity() {
            return initialCapacity;
        }

        public LocalRedis setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        @Override
        public AbstractCache newCache(String name, RedisCacheManager redisCacheManager) {
            return new L2Cache(name, redisCacheManager, this);
        }
    }
}
