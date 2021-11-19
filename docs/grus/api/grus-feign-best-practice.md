---
title: 最佳实践
---

# 巨灵神最佳实践和规范

## ServiceName

- 注解`com.ciicgat.api.core.annotation.ServiceName`里面填的 value，请写应用名，也就是 Frigate 里的应用名。需要保证严格一致、一模一样。
- 如果所有的接口，有统一的前缀(比如 userdoor 的业务接口 URL 前缀都统一为`http://userdoor.services.dev.ofc/userdoor`)，那么可以设置`urlPathPrefix`值，没必要在`RequestLine`里每条都加一个前缀。

## 方法请求

- 参数个数不要多余三个
- 一般情况推荐使用 application/x-www-form-urlencoded。
- 参数包含 List 或对象嵌套的情况多下，采用`application/json`方式提交，不要使用`application/x-www-form-urlencoded`
- 请求对象无需继承`java.io.Serializable`接口
- 业务模块可以为自己的所有请求类定义统一基类，命名可以统一为`XXXRequest`

## 方法返回

- 一般直接用对象作为方法返回，不要使用`ApiResponse<T>`或者`PhpApiResponse<T>`。由框架统一处理 code 非 0 时抛出的`BusinessFeignException`
- 如果需要获取 code 非 0 情况下 data 里的值，那么使用`ApiResponse<T>`。因为`BusinessFeignException`里面没有 data、它会把 data 抛弃。
- php 接口返回数据涉及到分页的情况，请使用`PhpApiResponse<T>`作为返回
- 不要使用 void 作为方法返回，void 的情况下，code 不等于 0 时，不会抛出`BusinessFeignException`
- 不要去修改 service 的返回对象，当作只读、不可变的使用 (后续我们可能会在框架层面做出约束，比如返回一个不可变的代理对象)
- 返回对象需要继承`java.io.Serializable`接口，因为用户可能会有序列化你的对象的需求。
- 业务模块可以为自己的所有返回类定义统一基类

## 返回码

- 业务模块为返回的 code 编写枚举类，枚举 code 列表。
- 枚举类需要继承`com.ciicgat.sdk.lang.convert.ErrorCode`

## 超时

- 框架默认的超时参数为：connectTimeout=10s、readTimeout=60s

- 接口提供方，可以通过`com.ciicgat.api.core.annotation.ApiTimeout`注解设置接口的**合理通用**超时参数。一般的：

  - 单条读接口，不要超过 500ms
  - 批量查询接口，不要超过 10s
  - 单条插入、更新接口，不要超过 500ms
  - 存在复杂事务的操作，不要超过 5s
  - 以上参数需要根据实际场景做调整

**我们的要求是，接口提供方必须设置 timeout 参数、尤其是 readTimeout。后续的 Merge Request，会审核，之前的服务需要补上**

- 接口使用方，可以通过`FeignServiceFactory.newInstance(final Class<T> serviceClazz, Request.Options options)`方法，设置自定义的 timeout。这个会覆盖`ApiTimeout`注解里的配置值，优先级最高。

## 异常处理

- 客户端需要处理所有的`BusinessFeignException`和`FeignException`
- 接口提供方，需要考虑使用者对你接口的依赖程度。比如如下场景：页面上有公告栏，公告内容从公告服务里获取。对于整个页面来说，公告栏不是必须的元素，可以没有。那么在调用公告服务的时候，假如它出现问题，其实对于全局是无影响的，它的错误是可以忽略的。这个时候，我们可能需要使用`try catch`。我们框架提供了特性`com.ciicgat.api.core.annotation.IgnoreError`，加在方法上面，可以完美适用这种场景。
- 可以使用`com.ciicgat.api.core.ServiceError`来获取`FeignException`的错误类型，然后友好输出给用户、或者打印到日志

## 缓存

- 一致性要求不高且对性能有要求的情况下，可以使用`com.ciicgat.api.core.annotation.ApiCache`

## 兼容性

- 服务端提供的实际接口需要严格保证兼容性
- 巨灵神的接口可以做破坏性的变更。依赖接口的调用方编译不会通过，强制他们修改代码，按照新的调用方法调用。

## 代码依赖性

- 对于返回类，我们的规范是写两次：巨灵神写一次、服务端写一次。服务端禁止依赖巨灵神里的模型。
- 巨灵神里的模型为服务模型，服务端里的模型你可以看作是 VO。同时，前者是后者的子集。

## 多语言支持

![多语言](../../assets/images/java/feign-language.png)

- 语言标识一般由 BFF 层从登录态获取，并通过 LanguageThreadLocal 设值。
- 后端 Services 的语言标识可以引入框架内的 LanguageFilter 进行统一设置。
- LanguageThreadLocal 可以识别当前使用线程的语言设置，并实现对应业务。
- 非 tomcat 线程需要自行传递语言设置到自定义线程中。
- 需要向下游自动传递语言标识的服务，需要在应用下配置 lang.properties 文件(任意内容，等后续升级)。
