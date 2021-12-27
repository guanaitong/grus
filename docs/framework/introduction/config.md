# 配置文件

**grus 所有配置，都是以 grus 作为前缀**

大家都可以通过 IDEA 轻松的输入。只需要输入`grus`前缀，idea 会自动把配置都联想出来，包括配置的含义。

![自动提示](../../assets/images/java/grus-auto.png)

原理：

大家可以查看 grus-boot 源代码，我们在`grus-boot-autoconfigure`模块的 pom 中，有以下依赖：

```xml
<!-- Annotation processing -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-configuration-processor</artifactId>
  <optional>true</optional>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-autoconfigure-processor</artifactId>
  <optional>true</optional>
</dependency>
```

这两个模块，会自动把`XXProperty`类的 field 的注释，生成为最终的描述。如：

```java
@ConfigurationProperties(prefix = "grus.swagger")
public class SwaggerProperty {
  /**
   * 基础扫描包
   */
  private String basePackage;
}
```
