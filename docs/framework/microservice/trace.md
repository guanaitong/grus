# 微服务链路追踪

## 简介

在grus使用了`jaeger`和`opentelemetry`技术做分布式链路追踪。grus2021.x是`jaeger`，grus2022.x后续新版本都是`opentelemetry`。两者已经做了兼容处理，能够识别对方的trace信息。

trace信息，已经通过`access`日志和a`pplicaton`日志打印出来，所以不再使用`jaeger-agent`或者`opentelemetry-collect`之类的工具收集了。

## 获取TraceId

新版本：

```java
        Span span = Span.current();
        if (span != Span.getInvalid()) {
            String traceId = span.getSpanContext().getTraceId();
            System.out.println(traceId);
        }
```

老版本：

```java
        SpanUtil.getSpanId(Spans.getRootSpan())
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

