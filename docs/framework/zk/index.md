# Zookeeper

如需使用 ZK 相关特性，首先在 POM 文件中添加

```xml
   <dependency>
      <artifactId>grus-boot-starter-zk</artifactId>
      <groupId>com.ciicgat.grus.boot</groupId>
   </dependency>

```

## 分布式锁

starter 所引用的 autoconfigure 模块默认会自动注入可生成分布式锁的工厂类 DistLockFactory，工厂类可以指定上锁资源并生成锁对象

```java
@Autowired
private DistLockFactory distLockFactory;

pubilc void doBiz(String resourceName){
  // 通过工厂类获取一个分布式锁
  DistLock distLock =  distLockFactory.buildLock(key);
  try{
    // 限时获取锁，未获取到锁时会阻塞，在超过给定时间后返回是否获取到锁的结果
    // 在zk异常时，获取锁也可能抛异常处理，所以请将结果返回false和抛异常都当作没获取到锁的标志
    boolean result = distLock.tryAcquire(1, TimeUnit.MINUTES);
    if (result) {
        // result为true表示获取到锁，执行业务逻辑
        doBizWithResource(resourceName);
    }else {
        // result 为false表示未获取到锁，执行自定义逻辑
        doSomethingWithNoLock();
    }
  } finally {
    // 正常执行或发生异常均释放锁
    // 最好根据获取锁的结果进行判断下，然后再执行释放锁的逻辑，不然会产生很多无用的报错日志
    distLock.release();
  }
}
```

以上为对一个共享资源使用前上锁，使用后释放的例子。

- 框架的可扩展性设计，开发者使用的是分布式锁的接口，其默认实现是通过 ZK 来完成的。框架可以透明地升级锁的内部实现，也可以升级提供基于其他中间件实现的分布式锁，并通过 gconf 配置灵活指定。

- 在对实际对资源加锁时，框架会在 ZK 生成锁路径时，默认传入当前启动的服务名（如 payment）。开发者传入的 key 名只需考虑服务内的命名区分。

##分布式 ID 生成器

starter 所引用的 autoconfigure 模块默认会自动注入 SnowflakeIdGenerator，用雪花算法生成 ID。Zookeeper 起到的作用是为服务内的不同机器分配机器码。

```java
// IdGenerator为公共接口，SnowflakeIdGenerator为其默认实现
@Autowired
private IdGenerator idGenerator;


public String genOrderNo(){
  // 序号 = yyyyMMdd + 16位十进制的整数（左补零）
  // 整数 = 机器码 10位 + 天秒数 17位 + 循环自增序列 17bit + 随机数 8bit = 52bit   16位十进制整数
  return idGenerator.makeNo();
}

public Long genId(){
  // 整数 = 天数 20位 + 天秒数 17位 + 机器码 10位 + 循环自增序列 17bit = 64bit
  return idGenerator.make();
}
```

- 生成器支持 String 和 Long 型两种序号类型，并且有其对应的生成规则。
- String 类型的序号生成时，日期前缀支持自定义，配置项为{grus.idgen.dateFormat}。但日期应最少包含 yyMMdd，以保证序号在 100 年内不会发生重复。
