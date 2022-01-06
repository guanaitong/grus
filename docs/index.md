---
hide:
- navigation
---

# 关爱通技术手册-JAVA

Java 统一封装了`grus`框架供大家使用开发，所以开发公司的项目，需保证项目的parent，统一使用`grus-boot-starter-parent`，如下：

```xml title="pom.xml"
<parent>
    <groupId>com.ciicgat.grus.boot</groupId>
    <artifactId>grus-boot-starter-parent</artifactId>
    <version>${suggested-version}</version>
</parent>
```

此 parent 类似 SpringBoot 的 parent，包含了日常开发中需要用到的 SpringBoot、Grus、Grus Boot、常用第三方等所有依赖。

在`grus`版本`2021.1.x`及以前，使用`jdk-11`，自`2022.0.0`（最新版本）开始使用`jdk-17`，详见 [Java17升级指南](framework/others/grus-17.md)

目前推荐的版本为 **`2021.1.17`**，基于`spring-boot-version`为`2.3.12.RELEASE`。

推荐使用的IDEA版本为`2021.2`及以上（后续会逐步转成`jdk-17`开发，建议使用支持`jdk-17`的IDEA版本）。

!!! warning

    1.0.0-SNAPSHOT 和早期版本已不再维护，请及时升级到推荐版本，保持技术栈的统一和程序的安全.

    自`2022.0`开始，框架和巨灵神的引用不再放在一起，并只支持了`jdk-17`：

    ```xml title="pom.xml"
    <project>
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>com.ciicgat.api</groupId>
                    <artifactId>ciicgat-agg</artifactId>
                    <version>2.0-SNAPSHOT</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
            </plugin>
        </plugins>
    </project>
    ```

## 发布版本

- 2022.0 - 在开发中 `2022.0.0-SNAPSHOT`，基于`spring-boot-version`为`2.5.8`
- 2021.1 - 推荐使用 `2021.1.17`，基于`spring-boot-version`为`2.3.12.RELEASE`

??? abstract "Old versions(不推荐使用)"

    - 2020.9 - 推荐使用 2020.9.2.RELEASE，于2021年6月1日不再维护
    - 2020.7 - 推荐使用 2020.7.3.RELEASE，于2021年3月1日不再维护
    - 2020.6 - 于2021年1月1日不再维护，已于2021年9月28日从maven仓库移除

## 升级日志

升级时，grus 一般都会有较大的变动，并提供一些有用的新特性。升级前建议仔细阅读 Release Notes，如下是相关的跳转链接。

- [2021.1 to 2022.0](https://gitlab.wuxingdev.cn/java/framework/grus/wikis/grus-2021.2-release-notes) - `TODO`
- [old to 2021.1](https://gitlab.wuxingdev.cn/java/framework/grus/wikis/grus-2021.1-release-notes), 这是一个从历史老版本升级的简单汇总

??? example "老版本升级"

    - [2020.7 to 2020.9](https://gitlab.wuxingdev.cn/java/framework/old/grus-boot/wikis/Grus-Boot-2020.9-Release-Notes)
    - [2020.6 to 2020.7](https://gitlab.wuxingdev.cn/java/framework/old/grus-boot/wikis/Grus-Boot-2020.7-Release-Notes)
    - [1.0.0 to 2020.6](https://gitlab.wuxingdev.cn/java/framework/old/grus-boot/wikis/Grus-Boot-2020.6-Release-Notes)

## 更多资料

- [grus-wiki](https://gitlab.wuxingdev.cn/java/framework/grus/wikis/home)
- [grus-代码库](https://gitlab.wuxingdev.cn/java/framework/grus)
- [关爱通知识库](https://guide.wuxingdev.cn)
