# Java17升级指南

使用 `https://adoptium.net/` 下载`jdk17`。它原来叫`adoptopenjdk`，后来项目归到`eclipse`旗下，改名为`eclipse temurin`，这也是我们线上采用的发行版。

也可以使用`idea`自带的jdk下载功能下载，选择`Eclipse Temurin`（idea最好配置下翻墙的http proxy）。

## POM修改

1. grus版本必须使用`2021.2.0-SNAPTSHOT`
2. `pom.xml`里增加以下配置：

    ```xml
    <project>
        <properties>
            <java.version>17</java.version>
        </properties>
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

大家可以查看示例项目 [open-business-travel](https://gitlab.wuxingdev.cn/biz/open/open-business-travel/blob/master/pom.xml)

## 依赖项升级

- junit升级到5；
- elasticJob升级为最新的`shardingsphere elasticjob`，相关包需要改为`org.apache.shardingsphere.elasticjob`；
- feign升级为11.6版本；
- TODO


## 兼容问题

TODO:其他兼容性问题带补充
