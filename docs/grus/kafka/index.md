# Kafka

当应用使用缓存、ES的时候，存在如下的需求：

1. 数据发生变更的时候，缓存如何刷新？
2. 数据发生变更的时候，ES的数据怎么刷新？

框架引入了 `Debezium` 提供一种解决方案。 [Debezium](https://debezium.io/) 是用于捕获变更数据的开源分布式平台。可以响应数据库的所有插入，更新和删除操作。通过监听到的 binlog
，以固定格式消息推送给 `Kafka`。Debezium 的消息格式 [点击查看](https://debezium.io/documentation/reference/1.5/connectors/mysql.html)。

框架针对 `kafka` 进行了 `Consumer` 能力的封装，对基于 `Kafka` 实现的 `Debezium` 消息处理进行了封装。

## 如何使用？

### 版本要求

> grus 版本 `2021.1.7+`

依赖坐标

```xml

<dependency>
  <groupId>com.ciicgat.grus</groupId>
  <artifactId>grus-kafka</artifactId>
</dependency>
```

### 配置消费者

```java

@Configuration
public class KafkaConfig {

  @Bean("debeziumMsgProcessor")
  public DebeziumMsgProcessor debeziumMsgProcessor(InventoryCategoryMsgProcessor inventoryCategoryMsgProcessor) {
    return new DebeziumMsgProcessor(inventoryCategoryMsgProcessor);
  }

  @ConditionalOnWorkEnv({WorkEnv.DEVELOP, WorkEnv.TEST, WorkEnv.PRODUCT})
  @Bean(initMethod = "start", destroyMethod = "close")
  public Consumer grusKafkaConsumer(@Qualifier("debeziumMsgProcessor") KafkaMsgProcessor kafkaMsgProcessor) {
    Consumer consumer = Consumer.newBuilder()
      .setTopics(kafkaConfig.getAllTopics())
      .setGroupId(getGroupId())
      .setPullThreadNum(2)
      .build();
    consumer.setKafkaMsgProcessor(kafkaMsgProcessor);
    return consumer;
  }
}
```

**PS：**

1. 如果只是单纯的消费 kafka 消息，消息处理器可以直接实现 `KafkaMsgProcessor`;
2. 如果监听的 kafka topic 是基于 debezium的，要实现 `DebeziumTableMsgProcessor`，并构建 `DebeziumMsgProcessor`。
3. `Debezium` 实现的 topic 规则：Mysql实例名.数据库名.表名。

* 开发环境：实例名固定值 `devapp`，例如：`devapp.ecommerce_baseproduct.InventoryCategory`
* 测试环境：实例名固定值 `testapp`，例如：`testapp.ecommerce_baseproduct.InventoryCategory`
* 生产环境：实例名为数据库所属实例。例如：`commerce.ecommerce_baseproduct.InventoryCategory`，数据库`ecommerce_baseproduct`
  在 `BSPRO-COMMERCE-MYSQL`。

4. 已经收集的Mysql 实例：

|       实例组名       |  topic 前缀      |
| ------------------ | ------     |
| BSPRO-APP-MYSQL |app|
| BSPRO-ASSET-MYSQL |asset|
| BSPRO-COMMERCE-MYSQL |commerce|
| BSPRO-ENDPRODUCT-MYSQL |endproduct|
| BSPRO-FINANCE-MYSQL |finance|
| BSPRO-INFOMANAGE-MYSQL |infomanage|
| BSPRO-MEMBER-MYSQL |member|
| BSPRO-PAYMENT-MYSQL |payment|

如果不知道数据库所属哪个实例组的可以到 [这里](https://frigate.wuxingdev.cn/dbInstanceGroup/toDataPage) 查询。

## 使用中可能遇到的问题

### Decimal (小数)

`debezium`
描述：[https://debezium.io/documentation/reference/1.5/connectors/mysql.html#mysql-decimal-types](https://debezium.io/documentation/reference/1.5/connectors/mysql.html#mysql-decimal-types)

为了保证精度，表列是 decimal 类型的，消息内容对应值是字符串的。可以使用注解 `@JsonDeserialize(using = DebeziumDecimalDeserializer.class)` 直接转成
BigDecimal。

```java

@Data
public class Demo {

  @JsonDeserialize(using = DebeziumDecimalDeserializer.class)
  private BigDecimal weight;
}
```

### Date （日期）

日期时间有 8 小时差距。可以使用注解`@JsonDeserialize(using = GMTPlus8Deserializer.class)`。

```java

@Data
public class Demo {

  @JsonDeserialize(using = GMTPlus8Deserializer.class)
  private Date timeCreated;
}
```

### 初次上线

debezium 已上线3个月+了，生产上已经收集了很多消息，当初次上线的时候会一次性消费掉所有消息（**最近一段时间kafka保留的相关binlog数据，并不是全量数据，请注意**），这里一定要考虑系统压力。电商遇到的一个情况是，搜索服务上线打印了消息日志，初次上线打印日志太多，应用本身有效日志收集不到了。

## 事件驱动

尽量基于事件驱动，获取到变更消息后，重新从db查询信息来操作，而不是完全基于消息给的参数变化。因为消息的消费绝对是有延后的，完全基于binlog的数据，很容易导致业务数据异常
