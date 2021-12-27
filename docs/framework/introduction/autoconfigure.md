---
title: autoconfigure模块
---

# autoconfigure 模块

## pom

autoconfigure 是 grus-boot 和 spring-boot 最重要的模块，两者设计架构和原理是一模一样的（grus-boot 学习的 spring-boot）。大家学会看 autoconfigure，那么对日常的工作有非常巨大的帮助。

大家看两者的 pom 文件，[grus-boot-autoconfigure-pom](http://gitlab.wuxingdev.cn/java/grus-boot/blob/master/grus-boot-autoconfigure/pom.xml#L40) 和 [spring-boot-autoconfigure-pom](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/pom.xml#L58)

大部分的依赖都是`<optional>true</optional>`。这个表示，只有 autoconfigure 源码编译时才存在依赖关系，最终应用引用 autoconfigure，其对那些 jar 其实不存在直接的依赖关系。

## spring.factories

SpringBoot 有类似于 Java SPI（Service Provider Interface）的机制，`spring.factories`主要用于 SpringBoot 实现一套用来被第三方实现或者扩展的 API。

grus-boot 的`spring.factories`文件内容：[grus-boot's spring.factories](http://gitlab.wuxingdev.cn/java/grus-boot/blob/master/grus-boot-autoconfigure/src/main/resources/META-INF/spring.factories)

springboot 在程序启动的时候，会按照文件中的顺序，依次实例化相关的 ContextInitializer 和 AutoConfiguration。这是一个自动化的过程。

这也是，springboot 实现开箱即用的基础。

通过这个文件，我们可以看到 grus-boot 在启动的时候，会自动加载什么模块。

## AutoConfiguration

AutoConfiguration 是 springboot 的核心，必须搞懂其工作机制。

举个例子：[OpenTracingAutoConfiguration](http://gitlab.wuxingdev.cn/java/grus-boot/blob/master/grus-boot-autoconfigure/src/main/java/com/ciicgat/grus/boot/autoconfigure/opentracing/OpenTracingAutoConfiguration.java)

`OpenTracingAutoConfiguration` 是加了 `@Configuration` 注解。该类的注释中可以看到其作用：

```java
// Indicates that a class declares one or more @Bean methods and 
// may be processed by the Spring container to generate bean definitions 
// and service requests for those beans at runtime, for example:
@Configuration
public class AppConfig {
    @Bean
    public MyBean myBean() {
        // instantiate, configure and return bean ...
    }
}
```

**大家需要对常用的 AutoConfiguration 类都有一个印象，知道其会自动注入哪些 Bean**。

举两个例子：

[GconfAutoConfiguration](http://gitlab.wuxingdev.cn/java/grus-boot/blob/master/grus-boot-autoconfigure/src/main/java/com/ciicgat/grus/boot/autoconfigure/gconf/GconfAutoConfiguration.java)

```java
@Configuration
@ConditionalOnClass(ConfigCollectionFactory.class)
@ConditionalOnProperty(prefix = "grus.gconf", value = "enabled", havingValue = "true", matchIfMissing = true)
public class GconfAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public ConfigCollectionFactory configCollectionFactory() {
        return RemoteConfigCollectionFactoryBuilder.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigCollection configCollection(ConfigCollectionFactory configCollectionFactory) {
        return configCollectionFactory.getConfigCollection();
    }
}
```

GconfAutoConfiguration 会自动 import`ConfigCollectionFactory`和`ConfigCollection`这两个对象，大家可以直接在自己的代码里直接 `@Autowired` 这两个对象。

[RedisAutoConfiguration](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/data/redis/RedisAutoConfiguration.java)

```java
@Configuration
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {

	  @Bean
	  @ConditionalOnMissingBean(name = "redisTemplate")
	  public RedisTemplate<Object, Object> redisTemplate(
			  RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
		    RedisTemplate<Object, Object> template = new RedisTemplate<>();
		    template.setConnectionFactory(redisConnectionFactory);
		    return template;
	  }

	  @Bean
	  @ConditionalOnMissingBean
	  public StringRedisTemplate stringRedisTemplate(
			  RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
		    StringRedisTemplate template = new StringRedisTemplate();
		    template.setConnectionFactory(redisConnectionFactory);
		    return template;
	  }
}
```

`RedisAutoConfiguration` 会自动引入`redisTemplate`和`stringRedisTemplate`这两个对象，大家可以直接在自己的代码里直接`@Autowired`这两个对象。

另外需要注意的是，通过`@Configuration`生效的`@Bean`，默认是被 aop 代理过的对象。如果不想要动态代理，可以把`@Configuration`换`@Component`。

## Conditional

springboot 提供了很多条件判断注解，只有在满足条件的时候，AutoConfiguration 才会被 Import 或者对象才会被 register 为 Bean。

两个非常重要、最常见的 Conditional 注解：

**@ConditionalOnClass**

使用举例：

```java
@Configuration
@ConditionalOnClass({FeignServiceBuilder.class, feign.Client.class})
@EnableConfigurationProperties({FeignProperties.class})
public class FeignAutoConfiguration {

}
```

含义：

当前应用程序 classpath 中存在 feign.Client 这个类文件，被修饰类 FeignAutoConfiguration 才会 import

**重点** ：@ConditionalOnClass 一般是用于配合 maven pom 的可选依赖。它是整个 SpringBoot 项目的一个核心。因为 autoconfigure 中基本都是可选依赖，运行时再通过 starter 把实际依赖引入进来，进而满足@ConditionalOnClass，实际的 AutoConfiguration 就会生效。**这个原理，大家必须掌握**。

**@ConditionalOnMissingBean**

使用举例：

```java
	@Bean
	@ConditionalOnMissingBean
	public StringRedisTemplate stringRedisTemplate(
			RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
```

含义：

当然 IOC 容器中不存在 redisTemplate 这个 bean 时，才会把 stringRedisTemplate()方法返回的对象作为 Bean 注册到 IOC 容器中。

重点：

- @ConditionalOnMissingBean 是 springboot 在**自动注入**与**用户自定义**之间的辅助。一方面，框架希望组件按照最佳实践都约定好、然后自动的都启动起来，不需要开发介入。另一方面，开发又存在自定的需求场景，或者每个公司对某些中间件的理解和使用不同，需要自定义。
- @ConditionalOnMissingBean 能够完美的解决上述的问题：框架提供了自动的 Bean，但是只有在开发没有自定义的情况下才会生效。
