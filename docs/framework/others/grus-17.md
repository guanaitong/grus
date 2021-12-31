# Java17升级指南

## Jdk安装

使用 `https://adoptium.net/` 下载`jdk17`。它原来叫`adoptopenjdk`，后来项目归到`eclipse`旗下，改名为`eclipse temurin`，这也是我们线上采用的发行版。
也可以使用`idea`自带的jdk下载功能下载，选择`Eclipse Temurin`（idea最好配置下翻墙的http proxy）。

## 升级与更新

### POM修改

1. grus版本必须使用`2021.2.0-SNAPTSHOT`
2. `pom.xml`里增加以下配置：

    ```xml
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
    </project>
    ```

3. maven-compiler-plugin里的11配置要去掉（如果有）：

    === "old"

        ```xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.0</version>
            <configuration>
                <source>11</source>
                <target>11</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
        ```

    === "new"

        ```xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
        </plugin>
        ```

大家可以参考示例项目 [open-business-travel](https://gitlab.wuxingdev.cn/biz/open/open-business-travel/blob/master/pom.xml)

### Bean复制工具

java17没法再使用`cglib`的`beancopier`，故`grus-core`中的`BeanCopyUtil`的实现方式略微变化，直接使用了`org.springframework.beans.BeanUtils`
，而不再是`org.springframework.cglib.beans.BeanCopier`。

### 单元测试

从junit4升级到junit5后，单元测试常用类产生了变更，需要开发者在版本升级后，检查并更新下单元测试。例如：

- `org.junit.Test`变更`org.junit.jupiter.api.Test`；
- `org.junit.Assert`变更`org.junit.jupiter.api.Assertions`等等。

使用`org.assertj.core.api.Assertions`等别的类库的断言类不会受影响。

### 安全检查

使用`Spring Boot Actuator`进行健康检查，替换原来的`isLive`。（`isLive`目前仍会保留）

应用启动时，自动注入的配置如下：

```properties
management.server.port=8181
management.endpoints.web.exposure.include=*
management.endpoint.health.probes.enabled=true
management.endpoint.health.show-details=always
management.endpoint.health.group.readiness.include=*
management.endpoint.health.group.readiness.show-details=always
management.endpoint.health.group.liveness.include=*
management.endpoint.health.group.liveness.show-details=always
#info信息，显示一些依赖项版本
info.build.grus-version=@grus.version@
info.build.java-version=@java.version@
```

通过HTTP暴露的endpoint如下：

- `127.0.0.1:8181/actuator/info`: 应用一些信息
- `127.0.0.1:8181/actuator/health`: 健康检查及应用的一些相关信息
- `127.0.0.1:8181/actuator/health/liveness`: 校验应用本身是否ok
- `127.0.0.1:8181/actuator/health/readiness`: 校验应用依赖的db, redis等是否ok

此时，请求info接口，会展示配置的各个依赖项版本，便于后期了解应用，返回如下：

```json title="GET http://127.0.0.1:8181/actuator/info"
{
    "build":{
        "grus-version":"2021.2.0-SNAPSHOT",
        "java-version":"17"
    }
}
```

## 依赖项升级

- junit升级到5（配套的单元测试都需要修改）；
- elasticJob升级为最新的`shardingsphere elasticjob`，相关包需要改为`org.apache.shardingsphere.elasticjob`；
- feign升级为11.6版本；
- `opentracing`移除，使用`opentelemetry`替代: `1.9.1`
- TODO

> `opentelemetry`是由两个开源项目`OpenTracing`和`OpenCensus`合并组成

## 兼容问题

TODO:其他兼容性问题带补充
