---
title: 使用指南
---

# 使用指南

## maven 依赖

```xml
<!-- 对于开发来说，一般不需要引入这个模块 -->
<dependency>
  <artifactId>grus-feign</artifactId>
  <groupId>com.ciicgat.grus</groupId>
</dependency>
 ```

## 自定义接口

```java
@Headers({"Content-Type: application/x-www-form-urlencoded"})
public interface MemberService {
    @RequestLine("POST /member/get")
    Member getMemberById(@Param("id") Integer var1);
}
```

## 自定义 ServiceName

在 Java 包中(package-info.java),配置 ServiceName 指向上面的 userdoor

```java
@ServiceName("userdoor")
package com.ciicgat.api.userdoor.service;
import com.ciicgat.api.core.annotation.ServiceName;
```

## 实例化接口

项目启动时，会扫描`@FeignService`，通过`BeanPostProcessor`自动将巨灵神API的服务注入，可直接使用。

> 也可以通过`@Bean`方式注入Spring容器使用，**但不再推荐**

```java
@Service
public class AppService{
    @FeignService 
    private PersonService personService;
    @FeignService(cacheBinding = @CacheBinding(method = "getPersonById", params = {0})) 
    private PersonService personService2;
}

// 下面这种写法不再推荐：
@Configuration
public class DependencyServiceConfig {
    @Bean 
    public PersonService personService() { 
        return FeignServiceFactory.newInstance(PersonService.class); 
    }
}
 ```

## 注解说明

**@Headers @RequestLine @Param**

> Feign 注解
> @Headers 请求头部
> @RequestLine 请求方式
> @Param 原生类型或其包装类型时，请求参数名称

**@ServiceName**

> **value** ：gconf 中 ciicgat-api 中 end_points.properties 或 end_points_k8s.properties 属性文件的 key 值
> **urlPathPrefix** ：url path 的前缀，适合在服务有一个统一前缀 path 时设置。

**注意** ：_此注解优先从类上获取，也即注在类上的注解优先于注解于包上的注解_
**注意** ：_value 的值一律与 gconf 的 appId 值相同_

```java
 @ServiceName(value = "card-mall-service",urlPathPrefix = "cardMallService")
```

**@PhpApiName**

> 在方法上加入此注解，请求 php 服务时自动会在请求体中加入"apiOutput=JSON&apiName="
> **value** ：请求 php 服务时，php 接口的名称，即上面的 apiName

```java
@RequestLine("POST")
@PhpApiName("CardSample.findAll")
List<CardSampleInfo> findCardSampleInfo(CardSampleInfoFindRequest request);
```

**@UrlFormBody**

> 当请求头部为@Headers("Content-Type: application/x-www-form-urlencoded")而请求参数为对象时，请求对象需加此注解

```java
@UrlFormBody
public class CardSampleInfoFindRequest implements java.io.Serializable {
    private String ids;
    private Integer enterpriseId;
    // ...
}
```

**@ApiCache**

> **params**：参数的序列号，按顺序组装缓存 key，不传将以方法名做 key，确保参数非空时 toString 不会给出"null"结果
> **expireSeconds**： 缓存过期时间
> **maxCacheSize**： 最大缓存大小
> **concurrencyLevel**：并发级别
> **cacheNullValue**：当值为 null 时，为 true（默认）将会缓存 null 值，为 false 的时候不会缓存 null 值

```java
@Headers("Content-Type: application/x-www-form-urlencoded")
@RequestLine("POST /getBean")
@ApiCache(params = {0, 1}, expireSeconds = 30)
TestBean getBean(@Param("text") String text, @Param("integer") int integer);
```

> 注意：缓存只会缓存成功的调用返回值；ApiCache 底层使用的缓存是 google 的 guava

**@IgnoreError**

> 调用时发生错误或其他异常，当返回为非基础对象时以空值返回，否则返回基础类型的默认值
> 使用方需要对接口返回的空值或默认值做正确的判断和处理
> 适用于聚合类接口，其中部分接口失败不影响整体返回

## Form 请求方式

> @Headers("Content-Type: application/x-www-form-urlencoded")
> 此方式为常用请求方式，当请求参数为对象时，注意在对象上加上注解 **@UrlFormBody**

```java
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface AdminRealmService {
    @RequestLine("POST /adminrealm/getAdminRealm2")
    AdminRealm getAdminRealm(@Param("appId") long appId, @Param("adminId") long adminId);
}
```

### GET 请求方式

GET 请求需要在 url 后面拼接参数，如下面的?personId={personId}

```java
@Headers("Content-Type: application/x-www-form-urlencoded")
@RequestLine("GET /push/findByPersonId?personId={personId}")
UserPushSetting findByPersonId(@Param("personId") Long personId);
```

### POST 请求方式

不同于 GET,POST 不需要再 url 后面拼接参数

```java
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface AdminRealmService {
    @RequestLine("POST /adminrealm/getAdminRealm2")
    AdminRealm getAdminRealm(@Param("appId") long appId, @Param("adminId") long adminId);
}
```

## Json 请求方式

此请求方式为 Json 格式请求方式，使用 Spring Controller 接收时加上 **@RequestBody** 注解

```java
public interface AppApi{
  @Headers({"Content-Type: application/json"})
  @RequestLine("POST /employee/batchGetEmployerNameByPersonIds")
  Map<Integer,String> batchGetEmployerNameByPersonIds(List<Integer> personIds); 
}
```

## Php 请求方式 <Badge>很少</Badge>

Php 请求以下面为例：

```shell
curl -d "apiOutput=JSON&apiName=CardInterface.checkCardStock&cardSampleIds=[114,151]" http://webservice.card.ciicgat.dev/
```

使用`@PhpApiName`时，会自动在头部加上`apiOutput=JSON&apiName=`。

```java
public interface AppApi{
  @RequestLine("POST")
  @PhpApiName("CardSample.findAll")
  List<CardSampleInfo> findCardSampleInfo(CardSampleInfoFindRequest request);
}
```

## 返回数据格式及异常处理

服务端返回的为 Json 格式，且**Java 端**的格式为：

```
{
    "code": 0,    //返回状态码
    "data": true, //data为实际返回数据
    "msg": "OK"   //返回错误消息
}
```

自定义的 feign 接口如果方法返回值为`com.ciicgat.sdk.lang.convert.ApiResponse`且返回 code 非 0 时，不会产生异常，反之若不为 ApiResponse 将会抛出一个`com.ciicgat.api.core.BusinessFeignException`异常信息

服务端返回的为 Json 格式，且**Php 端**的格式为：

```
{
    "result":true, //result为实际返回数据
    "success":"true",//返回结果
    "totalRows":"",//分页，总数
    "errorMsg":""//返回错误消息
}
```

自定义的 feign 接口如果方法返回值为`com.ciicgat.sdk.lang.convert.PhpApiResponse`且返回为 false 时，不会产生异常，反之若不为 PhpApiResponse 将会抛出一个`com.ciicgat.api.core.BusinessFeignException`异常信息

> 注意：PHP 涉及到分页请使用 PhpApiResponse 作为返回

## 自定义实例化工厂及超时和重试等配置

> `FeignServiceFactory.newInstance(MemberService.class)` 等价于`FeignServiceFactory.newInstance(MemberService.class, new Request.Options(), null, null)`,我们可以通过`FeignServiceFactory.newInstance`来自定义一些 feign 特殊的设置。

```
public static <T> T newInstance(Class<T> serviceClazz, Options options, Iterable<RequestInterceptor> requestInterceptors, Retryer retryer)

```

### FeignServiceFactory.newInstance 参数说明

**Options**：自定义连接超时时间（connectTimeoutMillis）和读取超时时间（readTimeoutMillis）

**RequestInterceptor**：feign 拦截器，若相对所有 request 做统一的处理这里可以使用此拦截器

**Retryer**：自定义异常请求重试次数，以及重试间隔以及最大重试间隔

## 自定义返回 Json 的解析格式

> ciicgat-sdk-api 处理返回 Json 格式的工具类是 Jackson 类库，因此我们可以使用 Jackson 提供的一些类来自定义一些返回数据的格式，如 BigDecimal 精度：

```
public class MoneyDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return jp.getDecimalValue().setScale(2, BigDecimal.ROUND_FLOOR);
    }
}
```

在返回的 model 上加上注解：

```
	@JsonDeserialize(using = MoneyDeserializer.class)
	private BigDecimal balance;
```

## 本地调试服务

有的时候，我们想调试本地服务，又不想改 gconf 上的配置，那么我们可以本地静态初始化如下代码，就可以配置 key 为 userdoor 的请求地址为http://localhost:19992

```java
@SpringBootApplication
public class Application{
  public static void main(String[] args) {
      EndPointConfigs.addEndPointConfig("userdoor", "http://localhost:19992");
      // ...
  }
}
```

## 查看测试用例

> sdk 包中给我们提供了大量的测试用例，查看测试用例可以帮助我们更快的理解和使用，用例地址：

http://gitlab.wuxingdev.cn/java/ciicgat-sdk/tree/master/ciicgat-sdk-api/src/test/java/com/ciicgat/api/core

# 参考文档

<p> <a href="https://github.com/OpenFeign/feign" target="_blank" title="https://github.com/OpenFeign/feign">OpenFeign Github地址</a></p>

<p> <a href="http://gitlab.wuxingdev.cn/java/ciicgat-sdk" target="_blank" title="http://gitlab.wuxingdev.cn/java/ciicgat-sdk">ciicgat-sdk-api GitLab地址</a></p>
