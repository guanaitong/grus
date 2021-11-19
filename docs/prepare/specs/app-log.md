---
title: 日志规范
---

# Java 日志规范

日志规范是对应用程序严格的约束，不遵守规范的日志，是进不了 ELK

## 日志规范

### access

- 日志输出目录：

```
logs/access.log
```

- 日志输出格式：

```
%t^%I^%a^%{x-app-name}o^%{x-app-instance}o^%{x-req-app-name}i^%{x-req-app-instance}i^%{x-trace-id}o^%{x-span-id}o^%{x-parent-id}o^%m^%U^%H^%s^%b^%D^%q
```

- 日志输出格式解析:
  - 日志以`^`作为分隔符
  - `%t`为请求时间
  - `%I`为处理线程
  - `%a`为访问者的 IP
  - `%{x-app-name}o`为当前应用名
  - `%{x-app-instance}o`为当前应用实例
  - `%{x-req-app-name}i`为请求应用名
  - `%{x-req-app-instance}i`为请求应用实例
  - `%{x-trace-id}o`为分布式 jaeger 追踪系统里的 traceId
  - `%{x-span-id}o`为分布式 jaeger 追踪系统里的 spanId
  - `%{x-parent-id}o`为分布式 jaeger 追踪系统里的 parentId
  - `%m`为 http 请求方法
  - `%U`为 http 请求 URL 里的路径
  - `%H`为 http 请求协议版本
  - `%s`为 http 请求返回码
  - `%b`为 http 请求发送信息的字节数，不包括 http 头，如果字节数为 0 的话，显示为-
  - `%D`为 http 请求处理消耗时间，单位毫秒
  - `%q`为 http 请求 URL 里的 query

举个栗子

```
[11/Dec/2018:00:00:02 +0800]^http-nio-80-exec-7^192.168.29.52^catalog^catalog-5849984c48-pbdj7^invoice-provider^invoice-provider-847ddbfcc-xd95v^3ec492be948636c2^ca71db0256d32de2^3ec492be948636c2^POST^/catalog/product/getByEntProductCategory^HTTP/1.1^200^558^11^
```

### application

- 日志输出目录：

```
logs/application.log
```

- 日志输出格式：

```
^V^ [%p] [%d{yyyy-MM-dd HH:mm:ss.SSS}] [%t] [%c:%L] [%X{traceId}:%X{spanId}:%X{parentId}] [%X{req.xRequestId}] %m%n
```

- 日志输出格式解析:
  - 以`^V^`作为换行符号（PS:因为内容里可能存在换行符`\n`，比如说错误堆栈，所以不能用`\n`）
  - `%p`为日志级别,即 DEBUG、INFO、WARN、ERROR、FATAL
  - `%d{yyyy-MM-dd HH:mm:ss.SSS}`为时间戳
  - `%t` 为输出产生该日志事件的线程名
  - `%c`为输出所属的类目，通常就是所在类的全名
  - `%L为`为日志事件的发生位置，及在代码中的行数
  - `%X{traceId}:%X{spanId}:%X{parentId}`为 jaeger 请求的 trace 信息。提示：**可以使用该值和 access 日志里的单条记录关联**，更加便于查询问题
  - `%X{req.xRequestId}`可以为由浏览器或者 app 传入的请求 id，或者为程序自己设置的上下文 id。默认为 jaeger 中的 traceId
  - `%m`为指定的日志内容
  - `%n`为回车换行符

举个栗子：

```
^V^ [INFO] [2018-12-11 10:12:46.004] [http-nio-80-exec-3] [com.ciicgat.catalog.provider.web.aop.ControllerAspect:58] [9fdc8467fd959175:c750076cbc96c3bd:6aab7c52ac856950] [9fdc8467fd959175] =====>>>>>requestInfo:catalog/param.getAllCommonParams()
```

## war 项目 <Badge>Deprecated</Badge>

<Alert type="error">
  不再使用 war 项目，统一使用 spring-boot，使用 jar 部署
</Alert>

## jar 项目

### access

对于开发、测试、生产环境的应用，程序在启动的时候，发布系统会自动注入以下参数

```properties
--server.tomcat.accesslog.directory=logs
--server.tomcat.accesslog.enabled=true
--server.tomcat.accesslog.pattern=%t^%I^%a^%{x-app-name}o^%{x-app-instance}o^%{x-req-app-name}i^%{x-req-app-instance}i^%{x-trace-id}o^%{x-span-id}o^%{x-parent-id}o^%m^%U^%H^%s^%b^%D^%q
--server.tomcat.accesslog.prefix=access
--server.tomcat.accesslog.suffix=.log
```

对于应用程序来说，无论是否在`application.properties`文件里配置了`server.tomcat.accesslog`选项，都会使用程序启动时命令行里的参数(命令行里优先级最高)。

所以开发无需在配置文件里配置`server.tomcat.accesslog`了。

### application

对于 SpringBoot 项目，`spring-boot-starter`默认集成了 **logback**。如果你选择 **logback**，那么不需要做 pom 的修改。

假如需要使用 **log4j2**，需要以下配置：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-logging</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

规范：**只允许使用默认实现或者 log4j2**

同 access 日志，发布系统同样会注入以下参数，开发也无需配置：

```properties
logging.file=logs/application.log
logging.file.max-history=7
logging.file.max-size=512MB
logging.pattern.file=^V^ [%p] [%d{yyyy-MM-dd HH:mm:ss.SSS}] [%t] [%c:%L] [%X{traceId}:%X{spanId}:%X{parentId}] [%X{req.xRequestId}] %m%n
```

详细的启动参数见：[应用启动脚本](../../prepare/specs/app-framework-spec.md#启动规范)

## 日志收集与查看

运维在每台服务器部署 filebeat,通过 filebeat 收集日志文件，将日志输出到`kafka`之中。然后通过`logstash`，处理`kafka`日志流，写入到`Elastic search`之中。最终，可以通过`kibana`查看。当然，日志是有一定的延迟。
