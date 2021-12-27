# 数据模块

## mysql driver

- mysql 驱动升级为 8.x。时区需要手动配置，框架已经针对时区做了兼容性处理，会在 JDBC 连接中，自动添加`serverTimezone=GMT%2B8`

## data

- 会根据配置，自动创建数据源。
- 如果需要读写分离，可以通过`DbProperties`来配置，如：

```
  grus.db.read-write-separation=true
```

- `DbProperties`还支持 jdbc 和连接池的扩展配置，如：

  ```
  grus.db.jdbcParams.useAffectedRows=true
  grus.db.dataSourceExtParams.connectionTimeout=30000
  ```

  注意，这里的配置会覆盖`datasource.json`里的配置。

- 自动创建数据源的时候，会根据 APP_NAME 去 gconf 里读取`datasource.json`的配置文件。该配置文件，是由 frigate 数据源管理系统自动写入 gconf 中的。无需大家手动维护。

- 数据源支持以下参数的动态配置，无须重启应用：

```
maximumPoolSize
minimumIdle
connectionTimeout
idleTimeout
maxLifetime
validationTimeout
leakDetectionThreshold
```

- 框架在数据源不同配置下生成的 bean

| 类                 | 对象名                  | 不启动读写分离                    | 启用读写分离                      |
| ------------------ | ----------------------- | --------------------------------- | --------------------------------- |
| DataSource         | dataSource              | y，等同于 masterDataSource        | y，等同于 masterDataSource        |
| DataSource         | masterDataSource        | y                                 | y                                 |
| DataSource         | slaveDataSource         | n                                 | y                                 |
| SqlSessionFactory  | sqlSessionFactory       | y，等同于 writeSqlSessionFactory  | y，等同于 writeSqlSessionFactory  |
| SqlSessionFactory  | writeSqlSessionFactory  | y                                 | y                                 |
| SqlSessionFactory  | readSqlSessionFactory   | n                                 | y                                 |
| SqlSessionTemplate | sqlSessionTemplate      | y，等同于 writeSqlSessionTemplate | y，等同于 writeSqlSessionTemplate |
| SqlSessionTemplate | writeSqlSessionTemplate | y                                 | y                                 |
| SqlSessionTemplate | readSqlSessionTemplate  | n                                 | y                                 |

**大家在迁移的时候，需要尤其需要注意 SqlSessionTemplate 的命名。**

- 如果你的项目中需要使用多数据源，那么需要调用`com.ciicgat.sdk.data.datasource.DataSourceFactory.createMasterDataSource(String dbName)`，传入自己的 dbName。配套的，SqlSessionFactory 和 SqlSessionTemplate 也需要创建。你也可以在 SQL 语句中，指定数据的 schema。
- 读写分离下，SqlSessionTemplate 的两个自动注入的对象为 writeSqlSessionTemplate 和 readSqlSessionTemplate

## mybatis

- mybatis 版本升级为 3.5.1
- [mybatis-3.5.0-Release-Notes](https://github.com/mybatis/mybatis-3/releases/tag/mybatis-3.5.0)
- [mybatis-3.5.1-Release-Notes](https://github.com/mybatis/mybatis-3/releases/tag/mybatis-3.5.1)
- mybatis 中，对于可能为 Null 的返回，推荐大家使用 Optional 做为返回
- 使用@Insert 注解时，不再支持返回值为自增加 id。
- mybatis 的配置，通过`MybatisProperties`来配置。比如：

```
mybatis.mapperLocations=classpath*:mapper/*.xml
mybatis.typeAliasesPackage=com.ciicgat.app.domain.entity
```

- 使用 mybatis 的 xml 方式时，需要在 applicaton.properties 文件中配置 mybatis.mapperLocations 和 mybatis.typeAliasesPackage

- 框架会自动加载`SQLLineInterceptor`，大家无需再手动设置

- 如果大家需要引入自定义的`Interceptor`，只需要注入它的 bean 即可：

  ```
   @Configuration
  public class DependencyService {

   @Bean
    public Interceptor interceptor() {
        return new MybatisPageInterceptor();
   }
  }
  ```

  原理在于：

  ```
  public class GrusMybatisAutoConfiguration implements InitializingBean {

      public GrusMybatisAutoConfiguration(DbProperties dbProperties, MybatisProperties properties,
                                          ObjectProvider<Interceptor[]> interceptorsProvider,
                                          ResourceLoader resourceLoader,
                                          ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                          ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
          this.dbProperties = dbProperties;
          this.properties = properties;
          this.interceptors = appendSystemInterceptor(interceptorsProvider.getIfAvailable());
          this.resourceLoader = resourceLoader;
          this.databaseIdProvider = databaseIdProvider.getIfAvailable();
          this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
      }
      }
  ```

  `GrusMybatisAutoConfiguration`的构造方法中，有一个`ObjectProvider<Interceptor[]>`的参数，spring 框架会自动 IOC 容器中的 Interceptor 都注入过来。

- 如果应用连接数据库，那么需要把数据库连接和 mybatis 设置相关的代码全部删除。采用 grus 的 data starter。同时去叫运维配置你的应用的数据库连接。完成后，在应用的 gconf 下会有`datasource.json`配置文件。对于未上线的新应用，大家可以拷贝一个已有应用的`datasource.json`文件，然后把相关配置改下，其中 encryptedPassword 需要改成 password，值使用明文。如下：

```
{
    "dbName": "ciicgatcard",
    "password": "1qazxsw@",
    "groupName": "GSDEV-NORMAL-MYSQL",
    "maxUserConnections": 50,
    "mysqlServers": [
        {
            "domain": "mdb.servers.dev.ofc",
            "groupName": "GSDEV-NORMAL-MYSQL",
            "ip": "10.101.11.106",
            "name": "GSDEV-NORMAL-MYSQL-01",
            "port": "3306",
            "proxyIp": "",
            "proxyPort": "",
            "role": "master",
            "version": ""
        }
    ],
    "params": {
        "minimumIdle": "1",
        "validationTimeout": "5000",
        "idleTimeout": "30000",
        "maximumPoolSize": "30",
        "connectionTimeout": "3000",
        "maxLifetime": "300000",
        "leakDetectionThreshold": "60000"
    },
    "username": "amy"
}
```

- 需要注意的是，由系统生成的 datasource.json，是只读的，你无法修改。

- 原先 SpringFramework 项目(非 Springboot)，如果在`aplicationcontext.xml`配置了 mybatis 扫描，那么在新的框架中，可以使用 MapperScan 注解代替,如：

```
@SpringBootApplication
@MapperScan(basePackages = {"com.gat.admin.dao"})
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .run(args);
    }
}


```

**如果 Dao 或者 Mapper 类上，已经加了@Repository 或者@Component 等标注 Spring Bean 的注解，那么无须再配置@MapperScan。否则，在一个特定的场景会触发一个 BUG。**所以，一般情况下，我们不推荐使用 MapperScan。

## 单元测试

如果原先使用了嵌入式数据库做单元测试，配置里有 DatabaseIdProvider，那么新的写法如下：、

```
  配置类中注入databaseIdProvider，系统会自动使用
  @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.put("MySQL", "mysql");
        properties.put("Apache Derby", "derby");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
```

本地数据源：

```
@EnableTransactionManagement
@SpringBootApplication
public class ApplicationTest {//单元测试的启动类
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }

    //注入本地数据源masterDataSource
    @Bean(name = {"dataSource", "masterDataSource"})
    @Primary
    public DataSource masterDataSource() {
        return TestDataSourceFactory.createDataSource("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:memory:myDB;create=true;");
    }

   //注入本地数据源slaveDataSource
    @Bean
    public DataSource slaveDataSource() {
        return TestDataSourceFactory.createDataSource("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:memory:myDB;create=true;");
    }


}
```
