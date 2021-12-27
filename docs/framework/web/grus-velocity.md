# Velocity 模块

从 SpringBoot1.5 版本开始，官方停止了 velocity 模板引擎的支持。SpringBoot2.0 之后，更是移除了 velocity 相关的适配代码。幸亏，阿里巴巴存在大量的 velocity 代码，其迁移到其他引擎的成本太高。所以，它对 velocity 有了比较有力的支持：<https://github.com/alibaba/velocity-spring-boot-project>

下面是使用的介绍，也可以看阿里 Github 上提供的使用文档。使用体验和 SpringBoot 其他模块引擎类似。

示例项目：<http://gitlab.wuxingdev.cn/java/giveapp-mgr>

pom 引入 starter：

```
        <dependency>
            <artifactId>grus-boot-starter-velocity</artifactId>
            <groupId>com.ciicgat.grus.boot</groupId>
        </dependency>
```

**注意，不要直接引用阿里巴巴的 starter，否则你迁移时需要改的东西很多。**

application 配置文件：

```
#velocity
spring.velocity.enabled = true
spring.velocity.resource-loader-path = classpath:/templates
spring.velocity.suffix = .vm
#layout默认是开启的，但我们大部分项目没有使用layout，所以需要关闭
spring.velocity.layout-enabled=false
spring.velocity.tools-expose-beans = true

#toolbox的路径
spring.velocity.toolboxConfigLocation = /toolbox/tools.xml
```

项目结构：

![velocity项目结构](../../assets/images/java/project-velocity.png)

toolbox 文件示例：

```
<?xml version="1.0" encoding="UTF-8"?>

<tools>
    <data type="number" key="TOOLS_VERSION" value="2.0"/>
    <data type="boolean" key="GENERIC_TOOLS_AVAILABLE" value="true"/>
    <toolbox scope="application">
        <tool class="org.apache.velocity.tools.generic.NumberTool"/>
        <tool class="org.apache.velocity.tools.generic.FieldTool"/>
        <tool class="org.apache.velocity.tools.generic.DateTool"/>
        <tool class="com.ciicgat.giveapp.mgr.tool.EnvUtil"/>
        <tool class="com.ciicgat.giveapp.mgr.tool.WebPageUtil"/>
    </toolbox>
    <toolbox scope="request">
        <tool class="org.apache.velocity.tools.view.ParameterTool"/>
    </toolbox>
</tools>
```

其中，你自己写的 tool 类，必须加上`@DefaultKey`注解，如上面的`com.ciicgat.giveapp.mgr.tool.EnvUtil`：

```
@DefaultKey("env")
public class EnvUtil {
    private static final String ENV = WorkRegion.getCurrentWorkRegion().getPublicDomainSuffix();

    public EnvUtil() {
        //
    }

    public String getDomainSuffix() {
        return ENV;
    }
}

```

**必须强调的是，toolbox 必须严格以上面方式来配置，否则不会生效**。（之前大部分项目都没有采取以上方式，需要大家手动调整下）
