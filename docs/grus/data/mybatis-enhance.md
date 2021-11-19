# Mybatis 增强

> grus-2021.1.0 开始支持

`code-generator`是一个简单的代码生成工具，在不侵入框架底层的前提下，达到简化开发、提高开发效率的目的。

此项目分为两个部分：第一个部分为**通用 Mapper 和 Service 提供 CRUD 基本操作的支持**，第二个部分是**代码生成**功能，为表结构变动时提供高效快速生成对应的实体对象。
此项目参考了[mybatis-generator](http://mybatis.org/generator/)和[mybatis-plus](https://mybatis.plus/), 取其精华并与 grus 框架融合。mybatis-generator 会生成很多冗余代码，而 mybatis-plus 则不用生成冗余代码，功能也很强大，但是其对底层侵入太深，对 mybatis 的 SqlSessionFactory 进行了重写，不能直接集成到我司项目中使用；参考两者优秀之处 code-generator 诞生了，既不需要生成冗余代码，也不需要对底层侵入，使用方便集成快速，适合快速开发，将更多的时间用在业务逻辑上。

## 特性

- **无侵入**：只做增强不做改变，引入它不会对现有工程产生影响
- **无损耗**：启动即会自动注入基本增删改查功能，性能无损耗，直接面向对象操作
- **CRUD 操作**：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
- **支持 Lambda 形式调用**：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
- **代码生成器**：可快速生成 Entity、Mapper、Service、Controller 层代码，支持自定义模板
- **分页**：基于 MyBatis 物理分页，直接返回 grus 框架封装的分页对象, 同时支持 pagehelper 分页插件

## 快速开始

这里以一个空的工程来示例

**添加依赖**

引入 Grus Boot Starter 父工程：

```xml
    <parent>
        <groupId>com.ciicgat.grus.boot</groupId>
        <artifactId>grus-boot-starter-parent</artifactId>
        <version>2021.1.0</version>
    </parent>
```

引入服务端应用常用的依赖

```xml
    <dependency>
        <artifactId>grus-boot-starter-server-general</artifactId>
        <groupId>com.ciicgat.grus.boot</groupId>
    </dependency>
```

引入数据依赖

```xml
    <dependency>
        <groupId>com.ciicgat.grus.boot</groupId>
        <artifactId>grus-boot-starter-data</artifactId>
    </dependency>
```

引入测试依赖

```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
```

**配置**

在`application.properties`中配置基本信息

```properties
spring.application.name=grus-demo
server.port=8088
server.tomcat.uri-encoding=UTF-8
logging.level.com.ciicgat.grusgenerator.codegenerator.example.mapper=debug
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8
grus.feign.log-req=true
grus.feign.log-resp=true
mybatis.mapper-locations=classpath*:mapper/*.xml

```

添加启动类`Application.java`

```java
@SpringBootApplication
@MapperScan("com.ciicgat.grusgenerator.codegenerator.example.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**编码**

编写实体类`ShopTip.java`

```java
@TableName("ShopTip")
public class ShopTip {
    @TableId
    private Long id;
    @TableField(ignoreSaving = true)
    private Date timeCreated;
    @TableField(ignoreSaving = true)
    private Date timeModified;
    private Long ecappId;
    private Integer type;
    private String title;
    private String content;
    private Integer enable;

  // 此处忽略 getter setter 方法
}
```

编写 Mapper 类`ShopTipMapper.java`

```java
public interface ShopTipMapper extends BaseMapper<ShopTip> {
   // 如有其他方法直接书写其中即可
   // ShopTip selectOne(Long id);
}
```

同时也支持`@Mapper`注解

**开始使用**

添加测试类

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleTest {
    @Resource
    private ShopTipMapper shopTipMapper;
    @Test
    public void testList() {
        Example<ShopTip> example = new ConditionExample<>();
        List<ShopTip> list = shopTipMapper.list(example);
        Assert.assertTrue(list.size() > 0);
        list.forEach(System.out::println);
    }
}
```

## CRUD 接口

### Mapper

通用 Mapper 接口：`com.ciicgat.sdk.data.mybatis.generator.template.BaseMapper.java`

⚠️ 特别提醒：Mapper 和 xml 中不能重载 BaseMapper 中的方法

**插入 Insert**

```java
// 选择性插入（仅插入非null字段）
int insert(T entity);
// 全字段插入（无论是否为null均进行插入）
int insertAll(T entity);
// 批量全字段插入（注：如表新加字段不为空时插入对象需要手动设置默认值）
int batchInsert(List<T> list);
```

**删除 Delete**

```java
// 根据ID删除
int delete(Long id);
// 批量ID删除
int batchDelete(List<Long> ids);
// 根据条件删除
int deleteByExample(Example<T> example);
```

**更新 Update**

```java
// 根据ID选择性更新
int update(T entity);
// 根据ID全字段更新 (无论是否为null均进行更新)
int updateAll(T entity);
// 根据条件选择性更新
int updateByExample(T entity, Example<T> example);
// 根据条件全字段更新
int updateByExampleAll(T entity, Example<T> example);
```

**获取 Get**

```java
// 根据ID获取实体
T get(Long id);
// 批量获取实体
List<T> batchGet(List<Long> ids);
// 根据条件获取实体
T getByExample(Example<T> example);
```

**查询数量 Count**

```java
// 根据条件数量查询
int count(Example<T> example);
```

**查询列表 List**

```java
// 根据条件列表查询
List<T> list(Example<T> example);
```

注：`Example`和`Query`都继承于`Conditional`, 在不同层面使用不同的接口：

- mapper 层: `Example` -> `ConditionExample`
- service 层: Query -> `QueryExample` , `PageQueryExample`

示例

```java
// mapper: Example
Example<ShopTip> example = new ConditionExample<>();
userMapper.getByExample(example);

// service: Query
Query<ShopTip> query = new QueryExample<>();
userService.getByExample(query);

Query<ShopTip> query1 = new PageQueryExample<>();
userService.page(query1);
```

### Service

通用 Service 接口：`com.ciicgat.sdk.data.mybatis.generator.template.BaseService.java`

⚠️ 特别提醒：继承 BaseService 前提需要对应的 Mapper 继承 BaseMapper

**插入 Insert**

```java
// 选择性插入（仅插入非null字段）
int insert(T entity);
// 全字段插入（无论是否为null均进行插入）
int insertAll(T entity);
// 批量全字段插入（注：如表新加字段不为空时插入对象需要手动设置默认值）
int batchInsert(List<T> list);
```

**删除 Delete**

```java
// 根据ID删除
int delete(Long id);
// 批量ID删除
int batchDelete(List<Long> ids);
// 根据条件删除
int deleteByExample(Query<T> query);
```

**更新 Update**

```java
// 根据ID选择性更新
int update(T entity);
// 根据ID全字段更新 (无论是否为null均进行更新)
int updateAll(T entity);
// 根据条件选择性更新
int updateByExample(T entity, Query<T> query);
// 根据条件全字段更新
int updateByExampleAll(T entity, Query<T> query);
```

**保存 Save**

> `entity.id == null` -> 插入
>
> `entity.id != null` -> 更新

```java
// 选择性保存
int save(T entity);
// 全字段保存
int saveAll(T entity);
```

**获取 Get**

```java
// 根据ID获取实体
T get(Long id);
// 批量获取实体
List<T> batchGet(List<Long> ids);
// 根据条件获取实体
T getByExample(Query<T> query);
```

**查询数量 Count**

```java
// 根据条件数量查询
int count(Query<T> query);
```

**查询列表 List**

```java
// 根据条件列表查询
List<T> list(Query<T> query);
```

**查询分页 Page**

```java
// 根据条件查询分页
Pagination<T> page(PageQueryExample<T> query);
```

## 条件构造器

> - 不建议将带条件构造器的接口直接暴露出去，应写一个 DTO 进行传输
> - 以下方法入参中的`R column`均表示数据库字段

### Conditional 条件构造器

**创建并且条件**

```java
// 创建条件: 如果条件列表为空则默认放入, 否则不放入
Criteria<T> createCriteria();
// 创建Lambda条件: 如果条件列表为空则默认放入, 否则不放入
LambdaCriteria<T> createLambdaCriteria();
```

**创建或者条件**

```java
// 设置或者条件
void or(GeneratedCriteria criteria);
// 创建或者条件, 并放入条件列表
Criteria<T> or();
```

**其他接口**

```java
// 清除条件和设置值
void clear();
// 设置是否去重
void setDistinct(boolean distinct);
// 设置offset: limit ${offset}, ${limit}
void setLimitStart(int limitStart);
// 设置limit: limit ${offset}, ${limit}
void setLimitEnd(int limitEnd);
// 添加排序，如未添加排序，则默认排序是id DESC
void addOrderBy(String columnName, boolean isAsc);
```

### Query 条件构造器

> 继承于 Conditional

**设置查询实体**

```java
// 设置实体: 将所有非空字段加入eq条件列表
void setEntity(T entity);
// 此方法可与Criteria条件叠加使用
```

**Criteria 条件**

**eq**

```java
eq(R column, Object val)
```

- 等于 =
- 例: `eq("name", "老王")`--->`name = '老王'`

**ne**

```java
ne(R column, Object val)
```

- 不等于 <>
- 例: `ne("name", "老王")`--->`name <> '老王'`

**gt**

```java
gt(R column, Object val)
```

- 大于 >
- 例: `gt("age", 18)`--->`age > 18`

**ge**

```java
ge(R column, Object val)
```

- 大于等于 >=
- 例: `ge("age", 18)`--->`age >= 18`

**lt**

```java
lt(R column, Object val)
```

- 小于 <
- 例: `lt("age", 18)`--->`age < 18`

**le**

```java
le(R column, Object val)
```

- 小于等于 <=
- 例: `le("age", 18)`--->`age <= 18`

**between**

```java
between(R column, Object val1, Object val2)
```

- BETWEEN 值 1 AND 值 2
- 例: `between("age", 18, 30)`--->`age between 18 and 30`

**notBetween**

```java
notBetween(R column, Object val1, Object val2)
```

- NOT BETWEEN 值 1 AND 值 2
- 例: `notBetween("age", 18, 30)`--->`age not between 18 and 30`

**like**

```java
like(R column, Object val)
```

- LIKE '%值%'
- 例: `like("name", "王")`--->`name like '%王%'`

**notLike**

```java
notLike(R column, Object val)
```

- NOT LIKE '%值%'
- 例: `notLike("name", "王")`--->`name not like '%王%'`

**likeLeft**

```java
likeLeft(R column, Object val)
```

- LIKE '%值'
- 例: `likeLeft("name", "王")`--->`name like '%王'`

**likeRight**

```java
likeRight(R column, Object val)
```

- LIKE '值%'
- 例: `likeRight("name", "王")`--->`name like '王%'`

**isNull**

```java
isNull(R column)
```

- 字段 IS NULL
- 例: `isNull("name")`--->`name is null`

**isNotNull**

```java
isNotNull(R column)
```

- 字段 IS NOT NULL
- 例: `isNotNull("name")`--->`name is not null`

**in**

```java
in(R column, Collection<?> value)
```

- 字段 IN (value.get(0), value.get(1), ...)
- 例: `in("age",{1,2,3})`--->`age in (1,2,3)`

```java
in(R column, Object... values)
```

- 字段 IN (v0, v1, ...)
- 例: `in("age", 1, 2, 3)`--->`age in (1,2,3)`

**notIn**

```java
notIn(R column, Collection<?> value)
```

- 字段 NOT IN (value.get(0), value.get(1), ...)
- 例: `notIn("age",{1,2,3})`--->`age not in (1,2,3)`

```java
notIn(R column, Object... values)
```

- 字段 NOT IN (v0, v1, ...)
- 例: `notIn("age", 1, 2, 3)`--->`age not in (1,2,3)`

---

### 示例

```java
// 创建条件构造器
Example<ShopTip> example = new ConditionExample<>();
// 创建第一个条件：id in (1,2,3) and type is not null
example.createCriteria().in("id", 1,2,3).isNotNull("type");
// 创建第二个条件： 因为第二个条件起不会默认放入条件列表, title like %关爱通%
LambdaCriteria<ShopTip> criteria = example.createLambdaCriteria().like(ShopTip::getTitle, "关爱通");
// 将第二个条件设置为或者条件
example.or(criteria);
// 创建第三个或者条件：timeCreated between '2020-09-11' and '2020-11-11'
example.orLambdaCriteria().between(ShopTip::getTimeCreated, "2020-09-11", "2020-11-11");
// 设置去重
example.setDistinct(true);
// 添加排序
example.addOrderBy("title", true);
example.addOrderBy("id", false);
List<ShopTip> list = shopTipMapper.list(example);
// 以上条件构造的SQL语句为：
/*
  SELECT DISTINCT `id`,`timeCreated`,`timeModified`,`ecappId`,`type`,`title`,`content`,`enable`
  FROM `ShopTip`
  WHERE ( `id` IN ( 1 , 2 , 3 ) and `type` IS NOT NULL )
  OR ( `title` LIKE CONCAT('%','关爱通','%') )
  OR ( `timeCreated` BETWEEN '2020-09-11' and '2020-11-11' )
  ORDER BY `title` ASC, `id` DESC;
 */
```

## 代码生成器

快速生成 Entity、Mapper、Mapper XML、Service、Controller 等各个模块的代码，极大的提升了开发效率

关于覆盖:

- Entity 文件: Entity 是与数据库表结构保持一致的, 尽量不要去修改, 如需扩展请添加 Bean 去继承它, 所以每次生成都会覆盖 Entity
- 其他文件: 其他文件一次生成之后可以添加自己的方法, 所以每次生成不会覆盖

### 快速开始

在前面那个项目上添加代码生成器

**添加依赖**

引入代码生成器，`scope`设置为`test`

```xml
    <dependency>
        <groupId>com.ciicgat.grusgenerator</groupId>
        <artifactId>grus-generator</artifactId>
        <version>${latest.version}</version>
        <scope>test</scope>
    </dependency>
```

**配置**

添加生成配置文件`code-generator.xml`, 建议放到测试资源目录

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE code-generator PUBLIC "-//guanaitong.com//DTD CodeGenerator 1.0//EN"
        "https://e.guanaitong.com/manual/code-generator.dtd">

<code-generator>
    <!--基础配置-->
    <baseConfig>
        <property name="enableSwagger">true</property>
        <property name="enableValidation">true</property>
    </baseConfig>

    <!--数据库连接-->
    <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                    host="mdb.servers.dev.ofc"
                    database="robin_example"
                    username="amy"
                    password="1qazxsw@"/>

    <!-- 生成实体文件 -->
    <javaModelGenerator targetPackage="com.ciicgat.grusgenerator.cg.example.bean.entity"
                        codePath="src/main/java"/>

    <!-- 生成Mapper的映射XML文件 -->
    <sqlMapGenerator targetPackage="mapper"
                     codePath="src/main/resources"/>

    <!-- 生成Mapper的类文件 -->
    <javaClientGenerator targetPackage="com.ciicgat.grusgenerator.cg.example.mapper"
                         codePath="src/main/java"/>

    <!-- 生成Service文件 -->
    <serviceGenerator targetPackage="com.ciicgat.grusgenerator.cg.example.service"
                      codePath="src/main/java"/>

    <!-- 生成Controller文件 -->
    <controllerGenerator targetPackage="com.ciicgat.grusgenerator.cg.example.controller"
                         codePath="src/main/java"/>
      <!-- 生成DTO文件 -->
    <dtoGenerator targetPackage="com.ciicgat.grusgenerator.test.cg.generated.dto"
                        codePath="src/test/java" suffix="VO"/>

    <!--需要生成的表-->
    <tables entityObjectSuffix="DO">
      <table tableName="User"/>
    </tables>

</code-generator>
```

> 配置的详细说明见下文 - 配置文档

**开始使用**

创建生成测试单元测试文件`CodeGeneratorTest.java`

```java
public class CodeGeneratorTest {
    @Test
    public void generate() {
        CodeGenerator.builder().build().generate();
    }
}
```

注：配置文件名为`code-generator.xml`时无需指定配置文件路径，否则需要指定`configFilePath`

### 读写分离

目前读写分离关爱通的读写分离是基于主从库的`SqlSessionTemplate`来实现的，目前项目中用读写分离的方法是创建一个`BaseDao`其中注入`readSqlSessionTemplate`和`writeSqlSessionTemplate`, 写 Mapper 的实现类，在其中使用对应的 SqlSessionTemplate 执行 Statment，为了保证读写分离操作的灵活性，code-generator 也沿用此使用习惯，使用`ReadWriteSeparationMapperImpl`中的增删改查方法是读写分离的。

**使用**

1. 配置文件`application.properties`添加读写分离的配置

```properties
grus.db.read-write-separation=true
```

2. 创建 Mapper 实现类，继承`ReadWriteSeparationMapperImpl`，添加注解`@Repository`,`@Primary`

```java
@Repository
@Primary // 说明①
public class TemplateMapperImpl extends ReadWriteSeparationMapperImpl<Template> implements TemplateMapper {

    @Override
    public Template getById(Long id) {
      // 说明②
        // return readSqlSessionTemplate.selectOne("com.ciicgat.grusgenerator.codegenerator.example.mapper.TemplateMapper.getById", id);
        return readSqlSessionTemplate.selectOne(this.getStatement("getById"), id);
    }
}
```

3. Mapper.xml 中添加自定义 SQL

```xml
<select id="getById" resultType="com.ciicgat.grusgenerator.cg.example.bean.entity.Template">
  select * from Template where id=#{id}
</select>
```

4. 调用处

```java
@Service
public class TemplateServiceImpl extends BaseServiceImpl<Template> implements TemplateService {

    // 说明③
    // @Resource(name = "templateMapperImpl")
    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public Template getById(Long id) {
        return templateMapper.getById(id);
    }
}
```

说明：

- ① 由于存在默认实现的`BaseServiceImpl`中会自动注入一个 TemplateMapper 的代理实例，所以增加 TemplateMapper 的实现类时需要用`@Primary`标注其为主要的，否则启动时 spring 会报错无法确认哪个实现类来注入
- ② 由于生成的 Mapper.xml 中的`namespace`是完整 Mapper 的路径，所以这边写 statement 时也需要写完整路径才能找到，为了简便封装了`getStatement`方法，所以直接用它即可简化调用
- ③ 由于`@Autowired`默认是按类型类自动装载，而`@Resource`默认是按名称来注入，而此时`TemplateMapper`存在 2 个实例，如果使用`@Resource`时需要指定名称，因为`@Primary`仅对`@Autowired`有效

**代码生成**

生成配置文件(`code-generator.xml`)的基础配置(`baseConfig`)中启用读写分离

```xml
<property name="enableReadWriteSeparation">true</property>
```

添加以上配置后生成的代码包含读写分离 Mapper 实现类，代码位置在`mapper.impl`包下

## 配置文档

`code-generator` 作为根元素，配置从此元素开始，以下说明下各个元素的含义与参数。

### baseConfig

**property 属性**

- **enableSwagger** 启用 Swagger，在生成的 DTO 和 Controller 中使用 Swagger 注解
- **enableValidation** 启用验证，在生成的 DTO 和 Controller 中使用验证注解，根据数据库表字段添加@NotNull,@Length 注解
- **enableReadWriteSeparation** 启用读写分离，生成读写分离对应的代码
- **disableUpdatingMapperXml** 禁用更新`MapperXml`文件，每次生成会同步更新 mapper 中`id=BaseColumnList`和`id=BaseResultMap`内容，如果不需要更新此项设置为 true 禁用更新
- **enableMapperAnnotation** 在生成的 Mapper.java 中添加@Mapper 注解，默认不启用

### jdbcConnection

| 属性          | 名称         | 是否必填 | 描述                                                                           |
| ------------- | ------------ | -------- | ------------------------------------------------------------------------------ |
| driverClass   | 驱动类       | 是       | jdbc 驱动类全路径                                                              |
| host          | 主机地址     | 是       | 数据库主机地址,如非默认端口`3306`需带上端口号                                  |
| database      | 数据库       | 是       | 数据库名称                                                                     |
| username      | 用户名       | 是       | 数据库用户名                                                                   |
| password      | 密码         | 是       | 用户名密码                                                                     |
| tinyInt1isBit | tinyint 转换 | 否       | 默认为 true, tinyint(1)类型转 Boolean; 为 false 时，tinyint(1)类型转为 Integer |

### javaModelGenerator

实体对象生成器

| 属性          | 名称           | 是否必填 | 描述                                                                               |
| ------------- | -------------- | -------- | ---------------------------------------------------------------------------------- |
| targetPackage | 包路径         | 是       | 文件所在的包全路径(小数点分隔的包名)                                               |
| codePath      | 代码地址       | 是       | 文件所在相对项目根目录的路径                                                       |
| disabled      | 是否禁用       | 否       | 禁用时，将跳过此模块生成                                                           |
| templatePath  | 自定义模板路径 | 否       | 模板路径是相对路径，需放于 resources 下，同名即覆盖，不同名需指定路径              |
| suffix        | 自定义后缀     | 否       | 为生成的模块指定固定后缀，此属性对 Entity 不生效，Entity 自定义后缀参考 table 标签 |

注：如果需要自定义代码模板，需要从默认模板复制一份到本地项目进行修改，复制后选择**无格式粘贴**，否则格式会混乱

默认模板文件路径

- **entity** templates/entity.java.ftl
- **mapperXml** templates/mapper.xml.ftl
- **mapperJava** templates/mapper.java.ftl
- **service** templates/service.java.ftl
- **serviceImpl** templates/serviceimpl.java.ftl
- **controller** templates/controller.java.ftl
- **dto** templates/dto.java.ftl

### sqlMapGenerator

XMLMapper 生成器，配置同上，默认后缀`Mapper`

### javaClientGenerator

JavaMapper 生成器，配置同上，默认后缀`Mapper`

### serviceGenerator

Service 生成器，配置同上，默认后缀`Service`

### controllerGenerator

Controller 生成器，配置同上，默认后缀`Controller`

### dtoGenerator

controller 入参 dto 的生成器，用于对象转换，配置同上。该元素依附于 controllerGenerator，若生成 controller 需要配置此项，默认后缀`DTO`。

### tables

要生成的表集合

| 属性               | 名称         | 是否必填 | 描述                                                      |
| ------------------ | ------------ | -------- | --------------------------------------------------------- |
| all                | 是否所有表   | 否       | 为 true 时，将读取数据库所有表, 将忽略其所有`table`子元素 |
| ignoreColumns      | 忽略的列     | 否       | 所有表需要忽略的列                                        |
| createTimeColumn   | 创建时间字段 | 否       | 默认为`timeCreated`                                       |
| updateTimeColumn   | 更新时间字段 | 否       | 默认为`timeModified`                                      |
| entityObjectSuffix | 实体后缀     | 否       | 给生成的实体加上统一后缀, 如 `DO` -> UserDO               |

**table 要生成的表**

| 属性             | 名称       | 是否必填 | 描述                                     |
| ---------------- | ---------- | -------- | ---------------------------------------- |
| tableName        | 表名       | 是       | 表名                                     |
| entityObjectName | 实体对象名 | 否       | 如果为空，则以表名大写驼峰作为实体对象名 |
| ignoreColumns    | 忽略的列   | 否       | 此表需要忽略的列                         |

注：

- 忽略的列将不在生成的 Entity 中展示，一般为不需要操作的可空列，`table`的`ignoreColumns`属性会继承`tables`的
- `entityObjectSuffix`和`entityObjectName`都存在时，生成的实体名为`${entityObjectName}${entityObjectSuffix}`

## 常见问题

### TINYINT(1)类型

1 个长度的 tinyint 为什么转为 Boolean 类型而不是 Integer？

> Mysql 官方参考文档关于布尔类型的说明：BOO, BOOLEAN
> These types are synonyms(同义词) for TINYINT(1). A value of zero is considered(认为是) false. Nonzero(不为 0) values are considered true.

解决方法：

1. 如果需要将 tinyint(1)转为 Integer 类型需要将配置文件中`jdbcConnection`的`tinyInt1isBit`属性显式设置为`false`
2. 将数据表 tinyint(1)改为 tinyint(2)

注：tinyint(1) 如果加了`unsigned`则 jdbc 读取到的长度是 3，对应转换为 Integer 类型

### 常见异常

- Mapped Statements collection already contains value for

  ```java
  java.lang.IllegalArgumentException: Mapped Statements collection already contains value for xxx.XxMapper.xxMethod
  ```

  原因：

  - Mapper 继承了 BaseMapper 之后，如果在 Mapper 中重载了 BaseMapper 中的方法，在应用启动时会出现如上异常；
  - Xml Mapper 中定义了与 BaseMapper 同名 sql 的 id

  解决：重命名该方法

- No qualifying bean of type

  ```java
  NoSuchBeanDefinitionException: No qualifying bean of type 'com.ciicgat.sdk.data.mybatis.generator.template.BaseMapper<xxx.Xxx>' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)
  ```

  原因：Mapper 所在的包未被扫描到

  解决：将其配置到`Application.java`的`@MapperScan`中

- Invalid bound statement (not found)

  ```java
  org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): xx.XxxService.xxMethod
  ```

  原因：

  - Mapper Xml 文件未被扫描
  - Mapper Xml 中无对应方法
  - Mapper Xml 与 Mapper 的 ResultType 不一致
  - MapperScan 未直接配置 Mapper 的包，可能只配置了 Mapper 的父包

  解决：

  - `application.properties`中配置 xml 路径 如:`mybatis.mapper-locations=classpath*:mapper/*.xml`
  - 检查并加入对应方法
  - 检查并使其一致
  - 配置`@MapperScan`到具体的 Mapper 所在的包，如果存在多个 mapper 包可考虑使用通配符

- BaseMapper could not be found

  ```java
  Field baseMapper in com.ciicgat.sdk.data.mybatis.generator.template.BaseServiceImpl required a bean of type 'com.ciicgat.sdk.data.mybatis.generator.template.BaseMapper' that could not be found.
  ```

  原因：只继承了 BaseService 没有继承 BaseMapper

  解决：对应的 Mapper 需继承 BaseMapper
