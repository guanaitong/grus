/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

import org.springframework.cache.Cache;

/**
 * Cache 配置参数
 *
 * @author wanchongyang
 * @date 2021/1/15 11:05 下午
 */
public abstract class CacheConfig {
    public static Local local() {
        return new Local();
    }

    public static Redis<Redis> redis() {
        return new Redis<>();
    }

    public static LocalRedis localRedis() {
        return new LocalRedis();
    }

    public abstract Cache newCache(String name, RedisCacheManager redisCacheManager);

    /**
     * Local specific cache properties.
     */
    public static class Local extends CacheConfig {
        /**
         * 是否序列化value，如果为否，那么缓存的value为对象，如果是，那么缓存的value为对象序列化后的字节数组
         * 不使用序列化性能更加高。但是需要保证返回的值，不能做修改。
         */
        private boolean serialize;

        /**
         * 缓存最大值
         */
        private long maximumSize = 102400L;

        /**
         * 过期时间，单位s
         */
        private int expireSeconds = 600;

        /**
         * 初始化大小
         */
        private int initialCapacity = 10240;

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
        public Cache newCache(String name, RedisCacheManager redisCacheManager) {
            return new LocalCache(name, redisCacheManager, this);
        }
    }

    /**
     * Redis-specific cache properties.
     */
    public static class Redis<R extends Redis> extends CacheConfig {
        /**
         * 使用gzip压缩存储缓存数据，缓存数据大时建议开启
         * <p>
         * 非NULL时，优先级最高
         */
        private Boolean useGzip;

        /**
         * 过期时间，单位s
         */
        private int expireSeconds = 3600;

        Redis() {
        }

        public Boolean getUseGzip() {
            return useGzip;
        }

        public R setUseGzip(Boolean useGzip) {
            this.useGzip = useGzip;
            return (R) this;
        }

        public int getExpireSeconds() {
            return expireSeconds;
        }

        public R setExpireSeconds(int expireSeconds) {
            this.expireSeconds = expireSeconds;
            return (R) this;
        }

        @Override
        public Cache newCache(String name, RedisCacheManager redisCacheManager) {
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
        private long maximumSize = 102400L;

        /**
         * local cache过期时间，单位s
         */
        private int localExpireSeconds = 3600;

        /**
         * 初始化大小
         */
        private int initialCapacity = 10240;

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
        public Cache newCache(String name, RedisCacheManager redisCacheManager) {
            return new L2Cache(name, redisCacheManager, this);
        }
    }
}
