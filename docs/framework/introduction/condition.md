# Condition 条件注解

grus 目前支持以下条件注解

## 环境条件注解

`ConditionalOnWorkEnv`：根据程序运行环境加载配置类

使用示例：

```java
@ConditionalOnWorkEnv({WorkEnv.DEVELOP, WorkEnv.TEST})
@Configuration
public class ElasticJobAutoConfiguration {
......
}

```

解释：只有在开发和测试，才会加载 ElasticJobAutoConfiguration 类

## 服务器环境注解

`ConditionalOnServerEnv`：根据程序是否运行在服务器环境加载配置类（大家本地电脑启动程序不算服务器环境）

使用示例：

```java
@ConditionalOnServerEnv
@Configuration
public class OpenTracingAutoConfiguration {
    ......
}
```

解释：在大家电脑本地启动程序，不会加载 OpenTracingAutoConfiguration。程序运行在任意环境的 linux 服务器里，会加载 OpenTracingAutoConfiguration

## Gconf key 条件注解

`ConditionalOnGconfConfigKey`：根据当前应用是否存在 gconf 的 key，判断是否需要加载配置类

使用示例：

```java
@Configuration
@ConditionalOnGconfConfigKey("redis.properties")
public class TestAutoConfigruation {
    ......
}

```

解释：假设当前应用是 userdoor，如果 userdoor 的 gconf 配置下存在配置文件`redis.properties`，那么会加载配置类 TestAutoConfigruation。否则不会加载。
