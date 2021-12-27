---
title: ElasticJob介绍
---

# 分布式任务调度 quick guide

## Maven 依赖

```
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-core</artifactId>
    <version>2.1.5</version>
</dependency>
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-spring</artifactId>
    <version>2.1.5</version>
</dependency>
```

`Elastic-Job`提供`Simple`、`Dataflow`和`Script` `3`种作业类型。一般我们用 Simple 基本可以解决大部分场景了
方法参数`shardingContext`包含作业配置、片和运行时信息。可通过`getShardingTotalCount()`, `getShardingItem()`等方法分别获取分片总数，运行在本作业服务器的分片序列号等。

## 1. Simple 类型作业

意为简单实现，未经任何封装的类型。需实现`SimpleJob`接口。该接口仅提供单一方法用于覆盖，此方法将定时执行。与`Quartz`原生接口相似，但提供了弹性扩缩容和分片等功能。

```java
public class MyElasticJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {
        switch (context.getShardingItem()) {
            case 0:
                // do something by sharding item 0
                break;
            case 1:
                // do something by sharding item 1
                break;
            case 2:
                // do something by sharding item 2
                break;
            // case n: ...
        }
    }
}
```

以上考虑对不同分片做不同的事情，对于集群中只有一台执行任务这种场景更简单，不用考虑分片信息，例如：

```java
public class MyJob implements SimpleJob {

	@Override
	public void execute(ShardingContext shardingContext) {
		//do something
	}

}
```

## 2. Dataflow 类型作业

`Dataflow`类型用于处理数据流，需实现`DataflowJob`接口。该接口提供`2`个方法可供覆盖，分别用于抓取(`fetchData`)和处理(`processData`)数据。

```java
public class MyElasticJob implements DataflowJob<Foo> {

    @Override
    public List<Foo> fetchData(ShardingContext context) {
        switch (context.getShardingItem()) {
            case 0:
                List<Foo> data = // get data from database by sharding item 0
                return data;
            case 1:
                List<Foo> data = // get data from database by sharding item 1
                return data;
            case 2:
                List<Foo> data = // get data from database by sharding item 2
                return data;
            // case n: ...
        }
    }

    @Override
    public void processData(ShardingContext shardingContext, List<Foo> data) {
        // process data
        // ...
    }
}
```

---

**流式处理**

可通过`DataflowJobConfiguration`配置是否流式处理。

流式处理数据只有`fetchData`方法的返回值为`null`或集合长度为空时，作业才停止抓取，否则作业将一直运行下去；
非流式处理数据则只会在每次作业执行过程中执行一次`fetchData`方法和`processData`方法，随即完成本次作业。

如果采用流式作业处理方式，建议`processData`处理数据后更新其状态，避免`fetchData`再次抓取到，从而使得作业永不停止。
流式数据处理适用于不间歇的数据处理。

## 3. Script 类型作业

`Script`类型作业意为脚本类型作业，支持`shell`，`python`，`perl`等所有类型脚本。只需通过控制台或代码配置`scriptCommandLine`即可，无需编码。执行脚本路径可包含参数，参数传递完毕后，作业框架会自动追加最后一个参数为作业运行时信息。

```
#!/bin/bash
echo sharding execution context is $*
```

作业运行时输出

`sharding execution context is {"jobName":"scriptElasticDemoJob","shardingTotalCount":10,"jobParameter":"","shardingItem":0,"shardingParameter":"A"}`

## 使用 Java 代码配置

**通用作业配置**

```java
// 定义作业核心配置
JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder("demoSimpleJob", "0/15 * * * * ?", 10).build();
// 定义SIMPLE类型配置
SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig, SimpleDemoJob.class.getCanonicalName());
// 定义Lite作业根配置
JobRootConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).build();

// 定义作业核心配置
JobCoreConfiguration dataflowCoreConfig = JobCoreConfiguration.newBuilder("demoDataflowJob", "0/30 * * * * ?", 10).build();
// 定义DATAFLOW类型配置
DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(dataflowCoreConfig, DataflowDemoJob.class.getCanonicalName(), true);
// 定义Lite作业根配置
JobRootConfiguration dataflowJobRootConfig = LiteJobConfiguration.newBuilder(dataflowJobConfig).build();

// 定义作业核心配置配置
JobCoreConfiguration scriptCoreConfig = JobCoreConfiguration.newBuilder("demoScriptJob", "0/45 * * * * ?", 10).build();
// 定义SCRIPT类型配置
ScriptJobConfiguration scriptJobConfig = new ScriptJobConfiguration(scriptCoreConfig, "test.sh");
// 定义Lite作业根配置
JobRootConfiguration scriptJobRootConfig = LiteJobConfiguration.newBuilder(scriptCoreConfig).build();
```

### Spring 命名空间配置

**由于框架在 misfire 上机制有问题，所以强烈建议每个任务的 misfire 都设成 false**

与`Spring`容器配合使用作业，可将作业`Bean`配置为`Spring Bean`，并在作业中通过依赖注入使用`Spring`容器管理的数据源等对象。可用`placeholder`占位符从属性文件中取值。`Lite`可考虑使用`Spring`命名空间方式简化配置。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:reg="http://www.dangdang.com/schema/ddframe/reg"
    xmlns:job="http://www.dangdang.com/schema/ddframe/job"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://www.dangdang.com/schema/ddframe/reg
                        http://www.dangdang.com/schema/ddframe/reg/reg.xsd
                        http://www.dangdang.com/schema/ddframe/job
                        http://www.dangdang.com/schema/ddframe/job/job.xsd
                        ">
    <!--配置作业注册中心 -->
    <reg:zookeeper id="regCenter" server-lists=" yourhost:2181" namespace="gat-job" base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3" />


    <!-- 配置简单作业 -->
    <job:simple id="simpleElasticJob" class="xxx.MySimpleElasticJob" registry-center-ref="regCenter" cron="0/10 * * * * ?" sharding-total-count="3" sharding-item-parameters="0=A,1=B,2=C" overwrite="true" misfire="false"/>
	<!-- 注意这里可以把overwrite配置成true，以防更改参数后zookeeper还是存储的原配置导致混淆 -->

    <!-- 配置数据流作业-->
    <job:dataflow id="throughputDataflow" class="xxx.MyThroughputDataflowElasticJob" registry-center-ref="regCenter" cron="0/10 * * * * ?" sharding-total-count="3" sharding-item-parameters="0=A,1=B,2=C" misfire="false"/>

    <!-- 配置脚本作业-->
    <job:script id="scriptElasticJob" registry-center-ref="regCenter" cron="0/10 * * * * ?" sharding-total-count="3" sharding-item-parameters="0=A,1=B,2=C" script-command-line="/your/file/path/demo.sh" />

    <!-- 配置带监听的简单作业-->
    <job:simple id="listenerElasticJob" class="xxx.MySimpleListenerElasticJob" registry-center-ref="regCenter" cron="0/10 * * * * ?" sharding-total-count="3" sharding-item-parameters="0=A,1=B,2=C" overwrite="true" misfire="false">
        <job:listener class="xx.MySimpleJobListener"/>
        <job:distributed-listener class="xx.MyOnceSimpleJobListener" started-timeout-milliseconds="1000" completed-timeout-milliseconds="2000" />
    </job:simple>

    <!-- 配置带作业数据库事件追踪的简单作业-->
    <job:simple id="listenerElasticJob" class="xxx.MySimpleListenerElasticJob" registry-center-ref="regCenter" cron="0/10 * * * * ?" sharding-total-count="3" sharding-item-parameters="0=A,1=B,2=C" event-trace-rdb-data-source="yourDataSource">
    </job:simple>
</beans>
```

具体每个标签具体支持的详细配置参考以下表格

### job:simple 命名空间属性详细说明

| 属性名                      | 类型    | 是否必填 | 缺省值 | 描述                                                                                                                                                                                                                                                                                                             |
| --------------------------- | :------ | :------- | :----- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| id                          | String  | `是`     |        | 作业名称                                                                                                                                                                                                                                                                                                         |
| class                       | String  | 否       |        | 作业实现类，需实现`ElasticJob`接口，脚本型作业不需要配置                                                                                                                                                                                                                                                         |
| registry-center-ref         | String  | `是`     |        | 注册中心`Bean`的引用，需引用`reg:zookeeper`的声明                                                                                                                                                                                                                                                                |
| cron                        | String  | `是`     |        | `cron`表达式，用于配置作业触发时间                                                                                                                                                                                                                                                                               |
| sharding-total-count        | int     | `是`     |        | 作业分片总数                                                                                                                                                                                                                                                                                                     |
| sharding-item-parameters    | String  | 否       |        | 分片序列号和参数用等号分隔，多个键值对用逗号分隔<br />分片序列号从`0`开始，不可大于或等于作业分片总数<br />如：<br/>`0=a,1=b,2=c`                                                                                                                                                                                |
| job-parameter               | String  | 否       |        | 作业自定义参数<br />作业自定义参数，可通过传递该参数为作业调度的业务方法传参，用于实现带参数的作业<br />例：每次获取的数据量、作业实例从数据库读取的主键等                                                                                                                                                       |
| monitor-execution           | boolean | 否       | true   | 监控作业运行时状态<br />每次作业执行时间和间隔时间均非常短的情况，建议不监控作业运行时状态以提升效率。因为是瞬时状态，所以无必要监控。请用户自行增加数据堆积监控。并且不能保证数据重复选取，应在作业中实现幂等性。<br />每次作业执行时间和间隔时间均较长的情况，建议监控作业运行时状态，可保证数据不会重复选取。 |
| monitor-port                | int     | 否       | -1     | 作业监控端口<br />建议配置作业监控端口, 方便开发者 dump 作业信息。<br />使用方法: echo "dump" \| nc 127.0.0.1 9888                                                                                                                                                                                               |
| max-time-diff-seconds       | int     | 否       | -1     | 最大允许的本机与注册中心的时间误差秒数<br />如果时间误差超过配置秒数则作业启动时将抛异常<br />配置为`-1`表示不校验时间误差                                                                                                                                                                                       |
| failover                    | boolean | 否       | false  | 是否开启失效转移<br />仅`monitorExecution`开启，失效转移才有效                                                                                                                                                                                                                                                   |
| misfire                     | boolean | 否       | true   | 是否开启错过任务重新执行                                                                                                                                                                                                                                                                                         |
| job-sharding-strategy-class | String  | 否       | true   | 作业分片策略实现类全路径<br />默认使用平均分配策略<br />                                                                                                                                                                                                                                                         |
| description                 | String  | 否       |        | 作业描述信息                                                                                                                                                                                                                                                                                                     |
| disabled                    | boolean | 否       | false  | 作业是否禁止启动<br />可用于部署作业时，先禁止启动，部署结束后统一启动                                                                                                                                                                                                                                           |
| overwrite                   | boolean | 否       | false  | 本地配置是否可覆盖注册中心配置<br />如果可覆盖，每次启动作业都以本地配置为准                                                                                                                                                                                                                                     |
| jobProperties               | String  | 否       |        | 作业定制化属性，目前支持`job_exception_handler`和`executor_service_handler`，用于扩展异常处理和自定义作业处理线程池                                                                                                                                                                                              |
| event-trace-rdb-data-source | String  | 否       |        | 作业事件追踪的数据源`Bean`引用                                                                                                                                                                                                                                                                                   |

#### job:dataflow 命名空间属性详细说明

job:dataflow 命名空间拥有 job:simple 命名空间的全部属性，以下仅列出特有属性

| 属性名            | 类型    | 是否必填 | 缺省值 | 描述                                                                                                                                    |
| ----------------- | :------ | :------- | :----- | :-------------------------------------------------------------------------------------------------------------------------------------- |
| streaming-process | boolean | 否       | false  | 是否流式处理数据<br />如果流式处理数据, 则`fetchData`不返回空结果将持续执行作业<br />如果非流式处理数据, 则处理数据完成后作业结束<br /> |

#### job:script 命名空间属性详细说明，基本属性参照 job:simple 命名空间属性详细说明

job:script 命名空间拥有 job:simple 命名空间的全部属性，以下仅列出特有属性

| 属性名              | 类型   | 是否必填 | 缺省值 | 描述                 |
| ------------------- | :----- | :------- | :----- | :------------------- |
| script-command-line | String | 否       |        | 脚本型作业执行命令行 |

#### job:listener 命名空间属性详细说明

`job:listener`必须配置为`job:bean`的子元素，并且在子元素中只允许出现一次

| 属性名 | 类型   | 是否必填 | 缺省值 | 描述                                                   |
| ------ | :----- | :------- | :----- | :----------------------------------------------------- |
| class  | String | `是`     |        | 前置后置任务监听实现类，需实现`ElasticJobListener`接口 |

#### job:distributed-listener 命名空间属性详细说明

`job:distributed-listener`必须配置为`job:bean`的子元素，并且在子元素中只允许出现一次

| 属性名                         | 类型   | 是否必填 | 缺省值         | 描述                                                                             |
| ------------------------------ | :----- | :------- | :------------- | :------------------------------------------------------------------------------- |
| class                          | String | `是`     |                | 前置后置任务分布式监听实现类，需继承`AbstractDistributeOnceElasticJobListener`类 |
| started-timeout-milliseconds   | long   | `否`     | Long.MAX_VALUE | 最后一个作业执行前的执行方法的超时时间<br />单位：毫秒                           |
| completed-timeout-milliseconds | long   | `否`     | Long.MAX_VALUE | 最后一个作业执行后的执行方法的超时时间<br />单位：毫秒                           |

#### reg:zookeeper 命名空间属性详细说明

| 属性名                          | 类型   | 是否必填 | 缺省值 | 描述                                                                                                         |
| ------------------------------- | :----- | :------- | :----- | :----------------------------------------------------------------------------------------------------------- |
| id                              | String | `是`     |        | 注册中心在`Spring`容器中的主键                                                                               |
| server-lists                    | String | `是`     |        | 连接`Zookeeper`服务器的列表<br />包括 IP 地址和端口号<br />多个地址用逗号分隔<br />如: host1:2181,host2:2181 |
| namespace                       | String | `是`     |        | `Zookeeper`的命名空间                                                                                        |
| base-sleep-time-milliseconds    | int    | 否       | 1000   | 等待重试的间隔时间的初始值<br />单位：毫秒                                                                   |
| max-sleep-time-milliseconds     | int    | 否       | 3000   | 等待重试的间隔时间的最大值<br />单位：毫秒                                                                   |
| max-retries                     | int    | 否       | 3      | 最大重试次数                                                                                                 |
| session-timeout-milliseconds    | int    | 否       | 60000  | 会话超时时间<br />单位：毫秒                                                                                 |
| connection-timeout-milliseconds | int    | 否       | 15000  | 连接超时时间<br />单位：毫秒                                                                                 |
| digest                          | String | 否       | 无验证 | 连接`Zookeeper`的权限令牌<br />缺省为不需要权限验证                                                          |

## 作业启动

### 1. Java 启动方式

```java
public class JobDemo {

    public static void main(String[] args) {
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("zk_host:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }

    private static LiteJobConfiguration createJobConfiguration() {
        // 创建作业配置
        ...
    }
}
```

### 2. Spring 启动方式

参见`Spring`命名空间

### 其他功能

### 1. 任务监听

可通过配置多个任务监听器，在任务执行前和执行后执行监听的方法。监听器分为每台作业节点均执行和分布式场景中仅单一节点执行`2`种。

#### 1. 每台作业节点均执行的监听

若作业处理作业服务器的文件，处理完成后删除文件，可考虑使用每个节点均执行清理任务。此类型任务实现简单，且无需考虑全局分布式任务是否完成，请尽量使用此类型监听器。

步骤：

- 定义监听器

```java

public class MyElasticJobListener implements ElasticJobListener {

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        // do something ...
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        // do something ...
    }
}
```

- 将监听器作为参数传入`JobScheduler`

```java
public class JobMain {

    public static void main(String[] args) {
        new JobScheduler(createRegistryCenter(), createJobConfiguration(), new MyElasticJobListener()).init();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("zk_host:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }

    private static LiteJobConfiguration createJobConfiguration() {
        // 创建作业配置
        ...
    }
}
```

### 2. 分布式场景中仅单一节点执行的监听

若作业处理数据库数据，处理完成后只需一个节点完成数据清理任务即可。此类型任务处理复杂，需同步分布式环境下作业的状态同步，提供了超时设置来避免作业不同步导致的死锁，请谨慎使用。

步骤：

- 定义监听器

```java

public class TestDistributeOnceElasticJobListener extends AbstractDistributeOnceElasticJobListener {

    public TestDistributeOnceElasticJobListener(long startTimeoutMills, long completeTimeoutMills) {
        super(startTimeoutMills, completeTimeoutMills);
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        // do something ...
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        // do something ...
    }
}
```

- 将监听器作为参数传入`JobScheduler`

```java
public class JobMain {

    public static void main(String[] args) {
        long startTimeoutMills = 5000L;
        long completeTimeoutMills = 10000L;
        new JobScheduler(createRegistryCenter(), createJobConfiguration(), new MyDistributeOnceElasticJobListener(startTimeoutMills, completeTimeoutMills)).init();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("zk_host:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }

    private static LiteJobConfiguration createJobConfiguration() {
        // 创建作业配置
        ...
    }
}
```

### 参考资料

[Elastic-Job-Lite 官方文档](http://dangdangdotcom.github.io/elastic-job/elastic-job-lite/02-guide/job-sharding-strategy/)
