---
title: 项目规范
---

# 项目规范

## 模块规范

1. 所有的项目，均为 SpringBoot 项目，最终编译为一个可运行的 fatjar。

2. 项目必须使用 grus 框架，符合 grus 框架的所有规范，比如 pom 的 parent 必须继承自`grus-boot-starter-parent`

3. pom 里必须设置`<build> <finalName>`标签，其值必须和最终上线的应用名一致。

4. `application.properties`里必须设置`spring.application.name`，其值必须和最终上线的应用名一致。

5. grus 的所有模块，一律通过 starter 的方式引入。

6. 尽量不要分太多模块，简单的纯后台项目一个模块搞定。

7. 包名以"com.ciicgat.xxx.yyy"开头

8. GAV 模板为：
```xml
    <!-- xxx为项目名，yyy为项目里的子模块 -->

   	<!-- parent:xxx -->

   	<groupId>com.ciicgat.xxx</groupId>
   	<artifactId>ciicgat-xxx</artifactId>
   	<version>实际版本号</version>

   	<!-- parent-module:yyy -->

   	<groupId>com.ciicgat.xxx</groupId>
   	<artifactId>ciicgat-xxx-yyy</artifactId>
   	<version>实际版本号</version>
```

## 启动规范

用订单服务项目举例，完整的应用启动命令如下（基于`grus-boot-starter-parent`为`2021.1`，`spring-boot-version`为`2.3.1.RELEASE`）：

> 随着`spring-boot`版本升级，有些`properties`会被废弃或者替换。这点在升级框架依赖的`spring-boot`版本时，需要格外注意，做好兼容处理。

```shell
exec /usr/local/openjdk-11/bin/java \
    -XshowSettings:vm \
    -Dclient.encoding.override=UTF-8 \
    -Dfile.encoding=UTF-8 \
    -Duser.language=zh \
    -Duser.region=CN \
    -XX:InitialRAMPercentage=50.0 \
    -XX:MaxRAMPercentage=85.0 \
    -XX:MetaspaceSize=128M \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=logs/heapDump.hprof \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+UnlockDiagnosticVMOptions \
    '-Xlog:gc*:file=logs/gc.log:time,tags:filecount=10,filesize=10240000' \
    -jar commerce-order.jar \
    --server.port=80 \
    --server.tomcat.max-threads=200 \
    --server.tomcat.basedir=. \
    --server.tomcat.accesslog.directory=logs \
    --server.tomcat.accesslog.enabled=true \
    '--server.tomcat.accesslog.pattern=%t^%I^%a^%{x-app-name}o^%{x-app-instance}o^%{x-req-app-name}i^%{x-req-app-instance}i^%{x-trace-id}o^%{x-span-id}o^%{x-parent-id}o^%m^%U^%H^%s^%b^%D^%q' \
    --server.tomcat.accesslog.prefix=access \
    --server.tomcat.accesslog.suffix=.log \
    --server.tomcat.accesslog.rotate=true \
    --server.tomcat.accesslog.rename-on-rotate=true \
    --server.tomcat.min-spare-threads=5 \
    --server.tomcat.uri-encoding=UTF-8 \
    --spring.boot.admin.client.url=http://spring-boot-admin-server/ \
    --spring.boot.admin.client.instance.prefer-ip=true \
    --management.server.port=8181 \
    '--management.endpoints.web.exposure.include=*' \
    --spring.cloud.sentinel.transport.dashboard=sentinel-dashboard \
    --spring.http.encoding.charset=UTF-8 \
    --spring.http.encoding.enabled=true \
    --logging.file.name=logs/application.log \
    --logging.file.max-history=7 \
    --logging.file.max-size=512MB \
    '--logging.pattern.file=^V^ [%p] [%d{yyyy-MM-dd HH:mm:ss.SSS}] [%t] [%c:%L] [%X{traceId}:%X{spanId}:%X{parentId}] [%X{req.xRequestId}] %m%n'
```
