# Mybatis 分页插件

## 目的

`grus` 框架对 `mybatis` 分页提供了统一的解决方案，基于 `PageHelper`，`GAV` 如下，默认**引入则启用**。

```xml
<dependency>
    <artifactId>grus-boot-starter-pagehelper</artifactId>
    <groupId>com.ciicgat.grus.boot</groupId>
</dependency>
```

> 常用的 `grus-boot-starter-server-service` 已引入上面的 `GAV`，自动启用，无需再引入。

## 使用需知

- 分页插件 PageHelper 介绍 [传送门](https://github.com/pagehelper/Mybatis-PageHelper)。
- PageHelper 使用说明 [如何使用](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md)。
- PageHelper 安全调用 [传送门](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md#3-pagehelper-%E5%AE%89%E5%85%A8%E8%B0%83%E7%94%A8)

### 如何禁用

如果项目中已经使用了 PageHelper 插件，与 `GrusPageHelperAutoConfiguration` 中自动注入的 `PageInterceptor` 冲突，或者因 `PageInterceptor` 插件引入导致某些正常查询报错，可以通过配置关闭，如下：

```properties
grus.pagehelper.enabled=false
```

### 配置参数差异化对比

> [PageHelper 插件](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md) 参数全部相同，前缀不同，个别参数默认值不同。

|                           | PageHelper | Grus-PageHelper |
| ------------------------- | ---------- | --------------- |
| 参数前缀                  | pagehelper | grus.pagehelper |
| `helperDialect`           | 自动检测   | mysql           |
| `supportMethodsArguments` | false      | true            |
| `autoRuntimeDialect`      | false      | true            |

- `helperDialect`

  分页插件会自动检测当前的数据库链接，选择合适的分页方式。 你可以通过配置来指定分页插件使用哪种方言。配置时，可以使用下面的缩写值：`oracle`,`mysql`,`mariadb`,`sqlite`,`hsqldb`,`postgresql`,`db2`,`sqlserver`,`informix`,`h2`,`sqlserver2012`,`derby`

- `supportMethodsArguments`

  支持通过 Mapper 接口参数来传递分页参数，默认值 `false`，分页插件会从查询方法的参数值中，自动根据 `params`（PageHelper 中的一个配置） 取值，查找到合适的值时就会参与分页。可以配置 `pageNum,pageSize,count,pageSizeZero,reasonable`，默认值为`pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero`

- `autoRuntimeDialect`

  设置为 `true` 时，允许在运行时根据多数据源自动识别对应方言的分页 （不支持自动选择`sqlserver2012`，只能使用`sqlserver`）

## 简单使用示例

### Lambda 写法（推荐）

> 这里只讲解推荐的写法，另外的方式请参考官方文档

```java
Page<User> page = PageHelper.startPage(1, 10).doSelectPage(()-> userMapper.selectGroupBy());
// 总数量
long totalCount = page.getTotal();
List<User> userList = page.getList();

// count 查询，返回一个查询语句的 count 数，注意不要有牵扯到数量的 order by
total = PageHelper.count(()->userMapper.selectLike(user));

// 紧跟在 PageHelper.startPage 方法后的第一个 Mybatis 的查询（Select）方法会被分页
// 这种方法也可以，但更推荐上面的那种，从 list 获取分页对象的方式见后面
PageHelper.startPage(1, 10);
List<User> list = userMapper.selectIf(1);
```

> count 查询，返回一个查询语句的 count 数，注意不要有牵扯到数量的 order by，PageHelper 会自动过滤掉 order by 的条件，详情见下文的 <a href="#手写 count 查询">手写 count 查询</a>

正常我们用不到 Page 中那么多参数，建议写成如下形式：

```java
PageSerializable<User> page = PageHelper.startPage(1, 10).doSelectPageSerializable(()-> userMapper.selectGroupBy());
long totalCount = page.getTotal();
List<User> userList = page.getList();
```

#### setOrderBy

支持自动拼接 order by，此时 sql.xml 中不应该有 order by 内容，写法如下：

```java
PageSerializable<User> page = PageHelper.startPage(1, 10).setOrderBy("id desc").doSelectPageSerializable(()-> userMapper.selectGroupBy());
```

#### <a name="手写 count 查询">手写 count 查询</a>

> PageHelper 默认的 count 查询，会忽略掉 order by，针对某些特殊业务的统计，有可能会产生问题。使用时，请一定要注意！！！

某些业务场景，分页时，希望手写 count 的 sql（提高性能） 或者 sql 中有影响行数统计的 order by（PageHelper count 查询时会自动过滤 order by 查询条件），会影响统计的总行数，PageHelper 提供了两种方式解决这个问题。

**手写 count sql（推荐）**

这时候，只需要在 sql.xml 中添加该 msId 对应的 count 写法即可。分页插件会优先通过当前查询的 msId + `countSuffix` 查找手写的分页查询。（`countSuffix` 默认为 `_COUNT`）示例如下：

```xml
<select id="selectLeftjoin" resultType="com.github.pagehelper.model.User">
    select a.id,b.name,a.py from user a
    left join user b on a.id = b.id
    order by a.id
</select>
<select id="selectLeftjoin_COUNT" resultType="Long">
    select count(distinct a.id) from user a
    left join user b on a.id = b.id
</select>
```

**sql 添加标记**

```sql
    select a.id,b.name,a.py from user a
    left join user b on a.id = b.id
    /*keep orderby*/order by a.id
```

> 将 `/*keep orderby*/` 加在需要 orderby 条件的前面

#### Reasonable

有些场景希望分页查询时，希望 `pageNum<=0` 时会查询第一页， `pageNum>pages`（超过总数时），会默认查询最后一页。可通过如下方式实现：

```java
PageInfo<Object> pageInfo = PageHelper.startPage(1, 10).setReasonable(true).doSelectPageInfo(() -> exchangeRecordMapper.findByEnterprise(copy));
// 实际页码
int pageNum = pageInfo.getPageNum();
// 实际页数
int pages = pageInfo.getPages();
```

除了指定某个语句外，也支持统一配置（不推荐）：

```properties
grus.pagehelper.reasonable=true
```

### 参数方法/对象调用

`grus.pagehelper.supportMethodsArguments=true` 时，该写法才可以生效（前面的写法不依赖此配置）。

> 并不推荐这种方式，除了需要更多的配置外，还容易产生一些使用不当的问题，这里只是简单罗列下有这种方式，给仍用这种方式的项目提供下 demo。

- 参数方法调用，存在以下 Mapper 接口方法，你不需要在 xml 处理后两个参数

  ```java
  //存在以下 Mapper 接口方法，你不需要在 xml 处理后两个参数
  public interface CountryMapper {
      List<User> selectByPageNumSize(
              @Param("user") User user,
              @Param("pageNum") int pageNum,
              @Param("pageSize") int pageSize);
  }
  //配置 supportMethodsArguments=true
  //在代码中直接调用：
  List<User> list = userMapper.selectByPageNumSize(user, 1, 10);
  ```

- 参数对象，如果 pageNum 和 pageSize 存在于 查询参数 对象中，只要参数有值，也会被分页

  ```java
  //如果 pageNum 和 pageSize 存在于 User 对象中，只要参数有值，也会被分页
  //有如下 User 对象
  public class User {
      //其他fields
      //下面两个参数名和 params 配置的名字一致
      private Integer pageNum;
      private Integer pageSize;
  }
  //存在以下 Mapper 接口方法，你不需要在 xml 处理后两个参数
  public interface CountryMapper {
      List<User> selectByPageNumSize(User user);
  }
  //当 user 中的 pageNum!= null && pageSize!= null 时，会自动分页
  List<User> list = userMapper.selectByPageNumSize(user);
  ```

* 从 list 取分页对象

  ```java
  //用PageInfo对结果进行包装
  PageInfo page = new PageInfo(list);
  //测试PageInfo全部属性
  //PageInfo包含了非常全面的分页属性
  assertEquals(1, page.getPageNum());
  assertEquals(10, page.getPageSize());
  assertEquals(1, page.getStartRow());
  assertEquals(10, page.getEndRow());
  assertEquals(183, page.getTotal());
  assertEquals(19, page.getPages());
  assertEquals(1, page.getFirstPage());
  assertEquals(8, page.getLastPage());
  assertEquals(true, page.isFirstPage());
  assertEquals(false, page.isLastPage());
  assertEquals(false, page.isHasPreviousPage());
  assertEquals(true, page.isHasNextPage());
  ```

> `@Param("pageNum") int pageNum` 中的命名可以根据 `grus.pagehelper.params` 配置，更多使用方式请参照 [文档](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md)。

## 注意事项

- **`PageHelper.startPage` 方法重要提示**

  只有紧跟在 `PageHelper.startPage` 方法后的<b>第一个</b> Mybatis 的<b>查询（Select）</b>方法会被分页。

- **`PageHelper` 默认的 count 会去除 order by**

  `PageHelper` 默认会去除自己写的 count 语句中的 order by 字段，有可能导致统计的总数不符。可以通过在 sql 语句中添加 `/*keep orderby*/` 注释解决该问题，或者通过手写 count 解决。

- **请不要配置多个分页插件**

  请不要在系统中配置多个分页插件(使用 Spring 时，`mybatis-config.xml`和`Spring<bean>`配置方式，请选择其中一种，不要同时配置多个分页插件)！

- **分页插件不支持带有`for update`语句的分页**

  对于带有`for update`的 sql，会抛出运行时异常，对于这样的 sql 建议手动分页，毕竟这样的 sql 需要重视。

- **分页插件不支持嵌套结果映射**

  由于嵌套结果方式会导致结果集被折叠，因此分页查询的结果在折叠后总数会减少，所以无法保证分页结果数量正确。

### 参数调用注意

- **对象还有成员变量 orderBy**

  当参数对象中有成员变量`orderBy`，`PageInterceptor`插件会在拦截的过程中做拼接排序的操作，这样最终执行的 sql 最后面会多`order by xxx`。这是`PageInterceptor`插件提供的默认功能，可配合分页参数一起使用。

  由于`grus-boot-starter-server-service`默认依赖`grus-boot-starter-pagehelper`，这样就导致依赖`grus-boot-starter-server-service`就默认集成了分页功能，就会导致参数对象包含`orderBy`成员变量的查询方法报错。如果出现此情况，使用以下三种方法解决：

  1. 使用分页插件功能，修改 xml 里面的 sql

  2. 配置`grus.pagehelper.enabled=false`，禁用`PageInterceptor`插件

  3. 修改`orderBy`成员变量名为其他

- **配置参数重要提示**

  当 `offsetAsPageNum=false` 的时候，由于 `PageNum` 问题，`RowBounds` 查询的时候 `reasonable` 会强制为 `false`。使用 `PageHelper.startPage` 方法不受影响。
