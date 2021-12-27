# freemarker 使用

前后端分离在我们公司还没有全面落地，很多页面会有使用服务端渲染方式开发的需要。这里列举一些常用的 freemarker 的技巧与注意事项。

## 配置

使用时，直接引入 `GAV` 即可（无特殊需求，大多数 freemarker 的配置无需配置，采用默认即可）。

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
        </dependency>
```

### 模板后缀 - suffix

新版本 `spring.freemarker.suffix`，默认为 `.ftlh`。老项目升级时得注意，建议手动设置 `spring.freemarker.suffix=.ftl`。

### 数字格式化 - number_format

freemarker 针对 `Integer` ，`Long` 等数字类型，在渲染时会默认添加千位分隔符，例如：`1000` 会渲染成 `1,000`。这对后续页面数据操作和跳转是非常麻烦的，需要去除。

单个操作可以通过 `${integer?c}`，来解决，但是很麻烦，强烈建议进行统一的设置，如下：

```properties
spring.freemarker.settings.number_format=0.##
```

意味着数字小数位最多展示两位，为 0 则隐藏，不会显示千位分隔符，例如：1001 渲染成 1001，1001.1 会渲染成 1001.1，1001.001 会渲染成 1001。

### 模板渲染异常

使用 freemark 渲染页面时，如果渲染不正确，页面上会出现默认的报错提示，可通过如下关闭：

```properties
spring.freemarker.settings.template_exception_handler=ignore
```

### 避免转义

在新的版本中，为了安全，如果渲染的字符串是 html 时，会默认进行转义操作，避免渲染的内容形成 dom 节点展示。

但是有的时候例如：自定义模板等场景，希望输出的字符串能够形成页面，需要关闭转义功能，即：`${previewTemplate?no_esc!''}`。

## 通用参数

### 全局变量

开发时，有时需要设置一些全局变量或全局函数，例如：静态资源地址，版本号等等，freemarker 支持相关写法，示例如下：

```java
@Configuration
public class FreeMarkerConfig {

    @Resource
    private freemarker.template.Configuration configuration;

    @Resource
    private FreeMarkerViewResolver freeMarkerViewResolver;

    @Resource
    private TestUtil testUtil;

    @PostConstruct
    public void setConfiguration() throws TemplateModelException {

        // 解决 由于默认兼容 HTTP 1.0 产生的 https 重定向后变成 http 问题
        freeMarkerViewResolver.setRedirectHttp10Compatible(false);

        if (WorkRegion.getCurrentWorkRegion().isDevelop() || WorkRegion.getCurrentWorkRegion().isTest()) {
            configuration.setSharedVariable("static_env", "tech");
        } else {
            configuration.setSharedVariable("static_env", "com");
        }
        configuration.setSharedVariable("g_env", AppConstant.PUBLIC_DOMAIN_SUFFIX);
        configuration.setSharedVariable("version", "1.0.0");
        configuration.setSharedVariable("testUtil", testUtil);
    }

}
```

### 登录变量

有些页面有一些公用的展示，例如：PC 页面展示的页头等。这时候，我们往往采用登录拦截器统一放置当前登录用户信息，避免在每个 controller 单独处理。示例如下：

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {

  	...

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        AppUser currentUser = ContextHolder.getCurrentUser();
        modelAndView.addObject("appUser", currentUser);
    }

  	...

}
```
