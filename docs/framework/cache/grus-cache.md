# Grus Cache

相对于 spring 默认实现（以及一些Provider实现时），grus cache的增加或者改进了以下功能：

- 如果数据反序列化或者获取数据失败，不会抛出异常，会 fallback到原始数据loader。
- 定义了本地缓存、redis缓存、二级缓存三种不同的场景
  - 使用本地缓存时，可全局更新（依赖redis的pub/sub机制）。使用体验与redis全局缓存一致。
  - 使用redis缓存时，如果redis挂了，会fallback到原始数据loader
  - 使用二级缓存时，如果redis挂了，不会影响业务
  - 可针对某个cache做精准的配置
- 增加了命中缓存后触发异步刷新机制
- 增加了统计数据
- 接入关爱通的redis数据源管理

新版本，大家可以在springboot admin后台管理自己的缓存。

## **grus 实现**

我们核心的实现逻辑入口：`com.ciicgat.sdk.springcache.RedisCacheManager`，对应的 autoconfig 为:`GrusRedisSpringCacheAutoConfiguration`。

**grus 实现会在 redis 连接出现问题或者数据反序列化失败时，走原始的数据获取渠道（如降级为读 mysql 里的数据）**

使用起来也很简单：

```
    @Bean
    public RedisCacheConfig redisCacheConfig(RedisSetting redisSetting) {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        redisCacheConfig.setPrefix("U_");
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setExpirePolicy(name -> 0);
        redisCacheConfig.setRedisSetting(redisSetting);
        return redisCacheConfig;
    }
```

注意：依然需要引入`spring-boot-starter-data-redis`包，依然需要在启动类上加上`@EnableCaching`注解

## **grus 二级缓存实现**

二级缓存实现，第一级本地缓存，使用的是现在主流的高性能缓存库 [Caffeine](https://github.com/ben-manes/caffeine/wiki)，`grus-core` 中默认依赖了 `caffeine`，基于 grus 框架开发的项目可以随意使用`caffeine`，也可以对比下 `guava cache`，使用过 `guava cache` 的很容易上手 `caffeine`，语法基本一样；
第二级还是 redis 缓存。

二级缓存版本依赖`grus`版本`2021.1.0`。

使用在原有`RedisCacheConfig`增加本地缓存配置：

```
redisCacheConfig.getLocalCacheConfig().setEnable(true).setGlobalEvict(true);

```

`LocalCacheConfig`本地缓存配置详细说明：

- `enable`：是否启用本地内存缓存，所有其他的配置生效的前提都是 enable=true；
- `globalEvict`：是否启用其全局删除机制（目前实现依赖于 redis 的 pub/sub 机制）；触发条件：使用注解`@CacheEvit`或者直接调用`RedisCache` or `L2Cache` 的 `evict()` 方法；
- `channel`：globalEvict=true 时，用于订阅删除 key 的通道名，如果不填写，默认使用应用名；
- `serialize`：是否序列化 value，如果为否，那么缓存的 value 为对象，如果是，那么缓存的 value 为对象序列化后的字节数组；不使用序列化性能更加高。但是需要保证返回的值，不能做修改。
- `maximumSize`：本地缓存最大值，默认 102400。

注意：本地内存还是尽量要过期，主要目的为了快速刷新缓存，所以本地缓存限制了最大缓存时间 **3600s**。默认本地缓存过期时间和`redis`一致（缓存时间<= 3600s）。虽然本地缓存限制了最大缓存时间，也不用担心性能问题，`redis`缓存未失效的情况下，数据一直都会重新塞入本地缓存。

## **基于缓存的fallback**

一些业务场景下，我们希望使用缓存来实现fallback机制，即：**正常情况下走业务接口，在业务接口异常或者超时时，走缓存**。

grus支持这种特性。可以使用两种方式来使用：

方式一，使用cacheconfig开启：

```java
CacheConfig.redis().setReverse(true);
CacheConfig.local().setReverse(true);
CacheConfig.l2().setReverse(true);

然后使用方法：
Cache.get(final Object key, final Callable<T> valueLoader)
```

方式二，使用AbstractCache特定方法：

```java
AbstractCache.getWithCacheFallBack(final Object key, final Callable<T> valueLoader)
```

推荐使用方法一。



## **grus 缓存新升级**

依赖`grus`版本 > `2021.1.2`。

新升级的缓存支持三种：`LocalCache`（本地缓存）、`RedisCache`（Redis 缓存）、`L2Cache`（二级缓存）；三种缓存可以同时使用，只需要构建对应的`CacheConfig`即可。

```
    @Bean
    public RedisCacheConfig redisCacheConfig(RedisSetting redisSetting) {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        redisCacheConfig.setRedisSetting(redisSetting);
        redisCacheConfig.setPrefix("GRUS_DEMO_3_");
        redisCacheConfig.setSerializer(RedisSerializer.java());
        redisCacheConfig.setUseGzip(true);
        redisCacheConfig.setCacheConfigFunc(name -> {
            switch (name) {
                case "useRedisCache":
                    return CacheConfig.redis().setExpireSeconds(600);
                case "useLocalCacheSerialize":
                    return CacheConfig.localRedis().setExpireSeconds(600).setLocalExpireSeconds(120).setSerialize(true);
                case "useLocalCache":
                    return CacheConfig.local().setExpireSeconds(60);
                case "useLocalCacheNoExpire":
                    return CacheConfig.local().setExpireSeconds(0);
            }

            return CacheConfig.localRedis();
        });
        return redisCacheConfig;
    }
```

**PS:**

- 1、本地缓存支持长期有效，只需过期时间设置为 0；
- 2、每个缓存的配置信息可以具体参见`CacheConfig`的`Local`、`Redis`、`LocalRedis`。
- 3、三种缓存都是基于`redis`的`pub/sub`机制实现的分布式缓存，所以即使只使用本地缓存，也需要配置`redis`。