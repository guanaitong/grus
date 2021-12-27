---
title: Starter模块
---

# Starter 模块

## 常用组合模块

grus 应用，一般只需要加入一个组合的 starter 依赖即可（常用的是`service`和`web`）。框架一共提供了 4 个组合 starter：

```xml
<!-- 此starter包含了在关爱通微服务架构中，服务端应用常用的最小的依赖 -->
<dependency>
  <artifactId>grus-boot-starter-server-minor</artifactId>
  <groupId>com.ciicgat.grus.boot</groupId>
</dependency>
```

```xml
<!-- 此starter包含了在关爱通微服务架构中，服务端应用常用的依赖 -->
<dependency>
  <artifactId>grus-boot-starter-server-general</artifactId>
  <groupId>com.ciicgat.grus.boot</groupId>
</dependency>
```

```xml
<!-- 此starter对应关爱通微服务架构中web层应用 -->
<dependency>
  <artifactId>grus-boot-starter-server-web</artifactId>
  <groupId>com.ciicgat.grus.boot</groupId>
</dependency>
```

```xml
<!-- 此starter对应关爱通微服务架构中service层应用 -->
<dependency>
  <artifactId>grus-boot-starter-server-service</artifactId>
  <groupId>com.ciicgat.grus.boot</groupId>
</dependency>
```

下面表格说明四个组合 starter 包含的模块：

| 模块                             | minor | general | web | service |
| -------------------------------- | ----- | ------- | --- | ------- |
| spring-boot-starter-aop          | ✓     | ✓       | ✓   | ✓       |
| spring-boot-starter-web          | ✓     | ✓       | ✓   | ✓       |
| spring-boot-starter-actuator     | ✓     | ✓       | ✓   | ✓       |
| spring-boot-admin-starter-client | ✓     | ✓       | ✓   | ✓       |
| grus-boot-starter-opentracing    | ✓     | ✓       | ✓   | ✓       |
| grus-boot-starter-web            | ✓     | ✓       | ✓   | ✓       |
| grus-boot-starter-fegin          | ✓     | ✓       | ✓   | ✓       |
| grus-boot-starter-gconf          |       | ✓       | ✓   | ✓       |
| grus-boot-starter-rabbitmq       |       |         | ✓   | ✓       |
| grus-boot-starter-data           |       |         |     | ✓       |
| grus-boot-starter-pagehepler     |       |         |     | ✓       |
| grus-boot-starter-gfs            |       |         | ✓   |         |

## 可选模块

对于 web 和 service 的应用，其他可选模块和规范如下：

| 模块                           | web 应用是否可选                                        | service 应用是否可选                                                                   |
| ------------------------------ | ------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| grus-boot-starter-redis        | 可选                                                    | 可选                                                                                   |
| grus-boot-starter-job          | 不可选。<br/>一般 web 应用不需要分布式任务支持。        | 可选                                                                                   |
| grus-boot-starter-validator    | 不可选。<br/>使用 SpringBoot 自带的 Hibernate-validator | 对于原来用到`ciicgat-boot-validato`r 的应用可选。<br/>其他一律使用 Hibernate-validator |
| spring-boot-starter-freemarker | 可选                                                    | 不可选。一般 service 应用不需要渲染页面。                                              |
| grus-boot-starter-velocity     | 对于原来使用到 velocity 模板的项目可选。                | 不可选                                                                                 |
| spring-boot-starter-thymeleaf  | 可选                                                    | 不可选。一般 service 应用不需要渲染页面。                                              |
| spring-session-data-redis      | 可选                                                    | 不可选。service 不需要 session                                                         |

**注意：**

- `grus-boot-starter-redis`会默认连接 redis 数据库，如果没有联系运维配置 `redis-config.json` 连接，那么项目启动会失败
