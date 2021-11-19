# 其他

## httpclient

<Alert type="warning">
  强烈建议使用框架封装的httpclient。
</Alert>

框架目前统一提供了`httpClient`的工具类 `com.ciicgat.sdk.util.http.HttpClientHelper`，供开发者使用，能满足绝大多数的使用场景。内置了如下功能：

- 优化好了线程池的大小，默认超时时间；
- 底层完全使用`okhttp`实现，移除了`apache http`依赖，以后禁止大家在自己的应用里手动添加`apache http`；
- 针对Dns处理进行了一定优化，见`com.ciicgat.sdk.util.http.CacheDns`；
- 添加了`trace`和监控报警，针对慢请求会记录并看情况进行frigate报警（企业微信收到通知）；
- 信任关爱通各个环境证书；
- 默认以字符串形式返回结果，也支持通过`HttpClientHelper.request`自定义处理，例如根据http响应码处理或者获取header中的数据,完成特殊场景下的http请求调用；
- 支持针对某个请求，通过`HttpTimeout`独立设置超时时间；
- 返回进行过处理，不将`Response`直接返回给使用方，避免因为**使用者处理不当，未关闭流而导致线上问题**。

以下是几个常见的`HttpClientHelper`使用方式介绍：

```java
public class AppTest {

  private static final Logger log = LoggerFactory.getLogger(AppTest.class);

  @Test
  public void test() {
    Map<String, Object> params = new TreeMap<>();
    params.put("grant_type", "client_credential");
    params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
    String url = "https://openapi.guanaitong.tech/token/create";

    // 普通的表单请求
    // public static String postForm(String url, Map<String, ? extends Object> formParameters)
    String response1 = HttpClientHelper.postForm(url, params);
    log.info("response1={}", response1);

    // 自定义超时时间的请求
    // public static String postForm(String url, Map<String, ? extends Object> formParameters, Map<String, String> headers, HttpTimeout httpTimeout)
    HttpTimeout httpTimeout = new HttpTimeout().connectTimeout(Duration.ofSeconds(5));
    String response2 = HttpClientHelper.postForm(url, params, null, httpTimeout);
    log.info("response2={}", response2);

    // 普通的json请求
    // public static String postJson(String url, String jsonBody)
    String response3 = HttpClientHelper.postJson(url, JSON.toJSONString(params));
    log.info("response3={}", response3);

    // 自定义handler请求，针对需要对httpStatus进行判断，或者别的需求，这是一个表单请求的示例
    // public static <T> T request(Request request, ResponseHandler<? extends T> responseHandler)
    FormBody.Builder formBody = new FormBody.Builder(StandardCharsets.UTF_8);
    params.forEach((key, value) -> {
      if (Objects.isNull(key) || Objects.isNull(value)) {
        return;
      }
      formBody.add(key, value.toString());
    });
    Request.Builder formRequest = new Request.Builder().post(formBody.build()).url(url);
    Result response4 = HttpClientHelper.request(formRequest.build(), response -> {
      // 此处response就是okhttp返回的Response
      int httpStatus = response.code();
      Result result = new Result();
      result.setHttpStatus(httpStatus);
      if (response.isSuccessful()) {
        try {
          if (response.body() != null) {
            String string = response.body().string();
            result = JSON.parse(string, Result.class);
            result.setHttpStatus(httpStatus);
          }
        } catch (IOException e) {
          log.warn("something wrong");
        }
      } else {
        // 响应码不正常，do something
        result.setMsg("do something");
      }
      return result;
    });
    log.info("response4={}", response4);
  }

  public static class Result {
    private int httpStatus;
    private Integer code;
    private String msg;

    public int getHttpStatus() {
      return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
      this.httpStatus = httpStatus;
    }

    public Integer getCode() {
      return code;
    }

    public void setCode(Integer code) {
      this.code = code;
    }

    public String getMsg() {
      return msg;
    }

    public void setMsg(String msg) {
      this.msg = msg;
    }

    @Override
    public String toString() {
      return JSON.toJSONString(this);
    }
  }

}
```

## 线程池的trace

在线上排查日志时，traceId是非常好用的工具。但是traceId一般仅在当前线程存在，当项目中使用了线程池时，traceId就丢失了，不利于后期问题的排查。
此时，项目中自定义的线程池需要使用框架封装的 `TraceThreadPoolExecutor.wrap` 包装下，使用委托类，或者直接就使用 `TraceThreadPoolExecutor` 作为线程池。

在多线程下，也支持 trace 的本质是对 `Runnable` 进行了包装，在方法调用前，自动在当前线程的 `MDC` 下设置相关字段，代码如下：

```java
public class TraceRunnable extends ThreadTraceHolder implements Runnable {
    
    private final Runnable runnable;

    public TraceRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        this.traceInject();
        this.runnable.run();
    }
}
```

```java
public class TraceThreadPoolExecutor extends ThreadPoolExecutor {
    
  public void execute(Runnable command) {
    if (command instanceof TraceRunnable) {
      super.execute(command);
    } else {
      super.execute(new TraceRunnable(command));
    }

  }

  public static Executor wrap(Executor executor) {
    return executor instanceof TraceThreadPoolExecutor ? executor : (command) -> {
      if (command instanceof TraceRunnable) {
        executor.execute(command);
      } else {
        executor.execute(new TraceRunnable(command));
      }

    };
  }
}
```

### 使用@Async注解的注意点

使用`@Async`注解时，使用的不是 `ThreadPoolExecutor`，是 `ThreadPoolTaskExecutor`，所以需要对现成进行包装操作，如下：

```java
@Configuration
@EnableAsync
public class AsyncExecutorConfiguration implements AsyncConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutorConfiguration.class);

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.setTaskDecorator(r -> {
            if (r instanceof TraceRunnable) {
                return r;
            } else {
                return new TraceRunnable(r);
            }
        });
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (arg0, arg1, arg2) -> {
            LOGGER.error("async execute exception:" + arg0.getMessage(), arg0);
            LOGGER.error("async execute exception method:{}", arg1.getName());
        };
    }
}
```

## security

推荐大家使用 spring-security 来加固你的 web 程序，处理`CSRF`、`XSS`等攻击。`Apache Shiro`不建议使用。

增加依赖：

```xml

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

文档：<https://docs.spring.io/spring-security/site/docs/5.1.5.RELEASE/reference/htmlsingle/>

## devtool

spring 为开发者提供了一个名为 spring-boot-devtools 的模块来使 Spring Boot 应用支持热部署，提高开发者的开发效率，无需手动重启 Spring Boot 应用。

```xml

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-devtools</artifactId>
  <optional>true</optional>
</dependency>
```

参考文档：<https://blog.csdn.net/jaydenwang5310/article/details/78738847>

## 其他

- `spring.main.allow-bean-definition-overriding`默认为 false，这个对大部分人没有影响。
- **如果你的项目成功迁移到新框架，那么 maven 依赖中，应该是没有任何老框架的 jar。因为新框架完全包含老框架的功能。否则，说明你的迁移还不完整**
