# 单元测试模块

大家可以看下这个文章：<https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html>和 springboot 官方的示例：<https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples/spring-boot-sample-test>

本文的示例，都是从官方示例中抽取的。

以下是核心内容：

pom.xml 里只需要加入以下依赖：

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
```

它**自动包含以下模块**：

- [JUnit 4](https://junit.org/)
- [Spring Test](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/testing.html#integration-testing)
- [AssertJ](https://joel-costigliola.github.io/assertj/)
- [Hamcrest](https://github.com/hamcrest/JavaHamcrest)
- [Mockito](https://mockito.github.io/)
- [JSONassert](https://github.com/skyscreamer/JSONassert)
- [JsonPath](https://github.com/jayway/JsonPath)

这些子模块，足够大家的项目中使用了。

下面详细讲解几种场景最新的写法

## Boot 单元测试基础

需要包含一个用于测试的 XXApplication,用于配置`@SpringBootTest`中的 classes。

比如：

```
@SpringBootApplication(exclude = {
        FeignAutoConfiguration.class,
        SwaggerAutoConfiguration.class,
        GrusDataAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        GrusWebAutoConfiguration.class,
        OpenTracingAutoConfiguration.class,
        ValidationAutoConfiguration.class})
@EnableElasticJob
public class JobApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(JobApplication.class)
                .run(args);

    }
}

```

```
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = JobApplication.class, //这里用到了JobApplication
        properties = {"spring.application.name=grus-demo", "grus.gconf.appId=grus-demo", "grus.job.serverLists=10.101.9.207:2182", "grus.job.namespace=grus-test-job"})
public class JobAutoConfigurationTests {


    @Autowired
    private GrusSimpleJob simpleJobTest;
    @Autowired
    private GrusSimpleJob2 simpleJobTest2;


    @Test
    public void test() {
        Threads.sleepSeconds(5);

        Assert.assertTrue(simpleJobTest.getValue() > 0);
        Assert.assertTrue(simpleJobTest2.getValue() > 0);

    }
}
```

## assert

大家统一使用 assertj 来做结果期望的判断。

一般的，使用`org.assertj.core.api.Assertions.assertThat`方法。异常使用通用的`org.assertj.core.api.Assertions.assertThatExceptionOfType`,也可以使用几个常见异常的方法，如`assertThatNullPointerException`、`assertThatIllegalArgumentException`等。

比如：<https://github.com/spring-projects/spring-boot/blob/master/spring-boot-samples/spring-boot-sample-test/src/test/java/sample/test/web/UserVehicleServiceTests.java#L77>

比如：<https://github.com/spring-projects/spring-boot/blob/master/spring-boot-samples/spring-boot-sample-test/src/test/java/sample/test/web/UserVehicleServiceTests.java#L67>

## Mock

对于 mock 的 bean，可以使用`@MockBean`来注入，如：

```
@MockBean
private UserVehicleService userVehicleService;

```

使用`org.mockito.BDDMockito.given`来做 mock，如：

```
org.mockito.BDDMockito.given(this.vehicleDetailsService.getVehicleDetails(VIN)).willReturn(new VehicleDetails("Honda", "Civic"));
```

原来诸如`Mockito.when(accountService.getById(Mockito.anyInt())).thenReturn(accountDO);`的写法不要使用了。

## web 层

MockMvc 注入方式：

```
@WebMvcTest(UserVehicleController.class) 类上，增加需要测试的Controller
class UserVehicleControllerTests{

   @Autowired
	private MockMvc mvc; //直接注入mvc即可
}


```

也可以使用`@AutoConfigureMockMvc`注解：

```
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
@AutoConfigureMockMvc
public class UserVehicleControllerTests {
    @Autowired
    private MockMvc mockMvc;
}
```

对于 web 层的结果判断，大家可以直接使用`andExpect`方法，不要使用 assertj 或者 junit 的 assert。

比如：

```
this.mvc.perform(get("/sboot/vehicle").accept(MediaType.TEXT_PLAIN))
.andExpect(status().isOk())//判断返回状态码
.andExpect(content().string("Honda Civic")); //判断字符串

this.mvc.perform(get("/sboot/vehicle").accept(MediaType.APPLICATION_JSON))
.andExpect(status().isOk())
.andExpect(content().json("{'make':'Honda','model':'Civic'}")); //判断json

```

以上示例可以见：<<https://github.com/spring-projects/spring-boot/blob/master/spring-boot-samples/spring-boot-sample-test/src/test/java/sample/test/web/UserVehicleControllerTests.java>>

## service 层

服务层可以把以来的 DAO 给 mock 掉，比如：

```
class UserVehicleServiceTests {

	private static final VehicleIdentificationNumber VIN = new VehicleIdentificationNumber("00000000000000000");

	@Mock
	private VehicleDetailsService vehicleDetailsService;

	@Mock
	private UserRepository userRepository;

	private UserVehicleService service;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.service = new UserVehicleService(this.userRepository, this.vehicleDetailsService);
	}

	@Test
	void getVehicleDetailsWhenUsernameIsNullShouldThrowException() {
		assertThatIllegalArgumentException().isThrownBy(() -> this.service.getVehicleDetails(null))
				.withMessage("Username must not be null");
	}
}
```

## 数据层

数据层的测试，一般都是需要嵌入式数据库的。最关键的是嵌入式数据库数据源的注入和其 schema 的创建。

写法一：

我们自己已经有一种写法：<https://guide.wuxingdev.cn/java/grus/grus-data.html#%E5%8D%95%E5%85%83%E6%B5%8B%E8%AF%95>

但是这种写法，只是提供了数据源，关于表的创建，还是需要额外的方法调用创建。

写法二：

SpringBoot 官方提供了`@AutoConfigureTestDatabase`注解。大家可以按照其约定很简单的就启动了注入嵌入式数据源，同时完成嵌入式数据库 schema 的创建。

有三个关键点：

1. 测试的类上需要加上@AutoConfigureTestDatabase。
2. classpath 中，需要增加 h2、derby、hsql 三个中任意一个包。
3. `src/test/resouces`文件夹下，增加 data.sql 和 schema.sql。

详细的，大家可以看`org.springframework.boot.test.autoconfigure.jdbc.TestDatabaseAutoConfiguration`

上面两种写法都是 OK 的，能达到目的即可。

## json

spring 提供了 JacksonTester，可以使用@JsonTest 帮助注入该对象，如：

```
@JsonTest
class VehicleDetailsJsonTests {

	@Autowired
	private JacksonTester<VehicleDetails> json;

	@Test
	void serializeJson() throws Exception {
		VehicleDetails details = new VehicleDetails("Honda", "Civic");
		assertThat(this.json.write(details)).isEqualTo("vehicledetails.json");
		assertThat(this.json.write(details)).isEqualToJson("vehicledetails.json");
		assertThat(this.json.write(details)).hasJsonPathStringValue("@.make");
		assertThat(this.json.write(details)).extractingJsonPathStringValue("@.make").isEqualTo("Honda");
	}

	@Test
	void deserializeJson() throws Exception {
		String content = "{\"make\":\"Ford\",\"model\":\"Focus\"}";
		assertThat(this.json.parse(content)).isEqualTo(new VehicleDetails("Ford", "Focus"));
		assertThat(this.json.parseObject(content).getMake()).isEqualTo("Ford");
	}

}
```

## JUNIT 5

junit 5 是 junit 4 下一代的测试框架，springboot 自 `2.3.*` 版本开始，也主推 junit 5。这里简单讲述下如何使用：

> junit 5 和 junit 4 相比，名称与功能基本不变，所属的包变了，所以如果进行升级操作的话，需要重新修改 package。
>
> @RunWith 被移除，用 @ExtendWith 替代

基于 grus 框架`2021.1`开始，使用 junit 5，依赖如下：

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

### 基于 spring 环境测试

```java
@SpringBootTest
public class FuelCardDaoTest {

    @Resource
    FuelCardDao fuelCardDao;

    @Test
    public void testBatchInsert() throws ParseException {
        List<FuelCardDO> fuelCards = new ArrayList<>();
        int expect = 1;
        for (int i = 0; i < expect; i++) {
            FuelCardDO fuelCardDO = new FuelCardDO();
            fuelCardDO.setOuterTradeNo(DateFormatUtils.format(new Date(), "MMddHHmm"));
            fuelCardDO.setCardNo(i + "" + new Date().toString());
            fuelCardDO.setAmount(new BigDecimal("10"));
            fuelCardDO.setEndDate(DateUtils.parseDate("2019-12-12", "yyyy-MM-dd"));
            fuelCards.add(fuelCardDO);
        }
        int actual = fuelCardDao.batchInsert(fuelCards);
        Assertions.assertEquals(expect, actual);
    }

    @Test
    public void testListByNos() {
        List<String> cardNos = Arrays.asList("10000016", "10000016", "10000025", "test");
        List<FuelCardDO> fuelCardDOS = fuelCardDao.listByNos(cardNos);
        System.out.println("fuelCardDOS = " + fuelCardDOS);
        System.out.println("fuelCardDOS.size() = " + fuelCardDOS.size());
        Assertions.assertNotNull(fuelCardDOS);
    }

}
```

不再需要`@RunWith(SpringRunner.class)`

### 基于 spring 环境 mock

```java
@SpringBootTest
public class FuelCardServiceTest {

    @Autowired
    private FuelCardService fuelCardService;

    @MockBean(name = "fuelCardDao")
    private FuelCardDao fuelCardDao;

    @Test
    public void test_0_getCardByCardNo() {
        FuelCardDO fuelCardDO = new FuelCardDO();
        fuelCardDO.setId(1);
        fuelCardDO.setCardNo("01");
        fuelCardDO.setOuterTradeNo("000");

        given(this.fuelCardDao.getByCardNo("00")).willReturn(null);
        given(this.fuelCardDao.getByCardNo("01")).willReturn(fuelCardDO);

        FuelCardResponse fuelCardResponse = fuelCardService.getCardByCardNo("01");
        assertThat(fuelCardResponse).isNotNull();
        assertThat(fuelCardResponse.getCardNo()).isEqualTo("01");
        assertThat(fuelCardResponse.getOuterTradeNo()).isEqualTo("000");

        assertThat(fuelCardDao.getByCardNo("00")).isNull();
    }

}
```

因为 mybatis-spring-boot-starter 的问题，目前如果需要 mock 数据库的操作，需要指定 name。该问题将在后续 grus 框架升级（升级 mybatis-spring-boot-starter 版本）后解决。

### 基于 spring 环境 mock 巨灵神

```java
@SpringBootTest
public class FuelTradeServiceTest {

    @Autowired
    private FuelTradeService fuelTradeService;

    @MockBean(name = "orderDetailDao")
    private OrderDetailDao orderDetailDao;

    @MockBean
    private FuelTradeAtomService fuelTradeAtomService;

    @Mock
    private ConsumeRecordService consumeRecordService;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(fuelTradeService, "consumeRecordService", consumeRecordService);
    }

    @Test
    public void test_dealOrderNotify_0() {
        CardOrderRequest cardOrderRequest = new CardOrderRequest();
        cardOrderRequest.setOuterTradeNo("01");
        String cards = "{\"cardNo\":\"12\",\"amount\":\"500.00\",\"endDate\":\"2019-01-01 12:12:12\"}";
        cardOrderRequest.setCards(cards);

        // 消费记录不存在
        given(consumeRecordService.get(Mockito.eq(null), Mockito.eq(null), Mockito.anyString())).willReturn(null);

        assertThatExceptionOfType(BusinessRuntimeException.class)
                .isThrownBy(() -> fuelTradeService.dealOrderNotify(cardOrderRequest))
                .withMessageContaining(BusinessErrorCode.OUTER_TRADE_NO_NOT_EXIST.getErrorCode() + "");
    }
}
```

因为通过`@FeignService`注入的服务，并没有交给 spring 容器管理，而是通过`BeanPostProcessor`，在项目启动时，扫描每个 class 中成员变量的注解，生成代理类替换实现的，所以无法用 `@MockBean` 注解。针对这种情况，如果需要 mock 外部服务的话，可以通过手工操作的方式解决，如上图中的：

```
    @Mock
    private ConsumeRecordService consumeRecordService;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(fuelTradeService, "consumeRecordService", consumeRecordService);
    }
```

手工生成巨灵神 API 的代理类，并替换测试类中的成员变量。

### 不依赖 spring 环境

有些时候，只想测试某个类自己的逻辑，不依赖外部的任何逻辑，可以完全不引入 spring 的相关环境。（做一个纯正的”单元“测试）

```java
@ExtendWith(MockitoExtension.class)
public class FuelCardServiceTest {

    @InjectMocks
    private FuelCardServiceImpl fuelCardService;

    @Mock
    private FuelCardDao fuelCardDao;

    @Test
    public void test_0_getCardByCardNo() {
        FuelCardDO fuelCardDO = new FuelCardDO();
        fuelCardDO.setId(1);
        fuelCardDO.setCardNo("01");
        fuelCardDO.setOuterTradeNo("000");

        given(this.fuelCardDao.getByCardNo("01")).willReturn(fuelCardDO);

        FuelCardResponse fuelCardResponse = fuelCardService.getCardByCardNo("01");
        assertThat(fuelCardResponse).isNotNull();
        assertThat(fuelCardResponse.getCardNo()).isEqualTo("01");
        assertThat(fuelCardResponse.getOuterTradeNo()).isEqualTo("000");
    }
}
```

或者

```java
public class FuelCardServiceTest {

    @InjectMocks
    private FuelCardServiceImpl fuelCardService;

    @Mock
    private FuelCardDao fuelCardDao;

    @BeforeEach
    public void initMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_0_getCardByCardNo() {
        FuelCardDO fuelCardDO = new FuelCardDO();
        fuelCardDO.setId(1);
        fuelCardDO.setCardNo("01");
        fuelCardDO.setOuterTradeNo("000");

        given(this.fuelCardDao.getByCardNo("01")).willReturn(fuelCardDO);

        FuelCardResponse fuelCardResponse = fuelCardService.getCardByCardNo("01");
        assertThat(fuelCardResponse).isNotNull();
        assertThat(fuelCardResponse.getCardNo()).isEqualTo("01");
        assertThat(fuelCardResponse.getOuterTradeNo()).isEqualTo("000");

    }

}
```

这两种初始化的方式完全是一样的。
