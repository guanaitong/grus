# Frigate 消息通知 API 使用说明

## 方法

大家一般都通过 FrigateNotifier 类发送消息，包括框架。

暴露给大家的方法，一般只需要指定 receiver、chanel、content、stack。

但其实，最终送到给 frigate 消息系统的内容，远远不止这几个字段。其他的字段，都由 api 自动附上了值。

大家可以通过 FrigateRawNotifier 来更高级的定制你的内容。

## 消息体

介绍下送达给 frigate 消息系统的消息体：

```
public class FrigateMessage {
    /**
     * 发送渠道，默认通过1为企业微信通知
     */
    private Integer channel = 1;
    /**
     * 消息标题
     */
    private String title = "frigate 消息通知";
    /**
     * 消息内容
     */
    private String content;
    /**
     * 当有异常堆栈时，堆栈内容
     */
    private String stack;
    /**
     * 模块
     */
    private String module;
    /**
     * 标签
     */
    private Map<String, String> tags;

    /**
     * ------------------以下属于系统变量------------------------
     **/

    private String traceId;
    private String hostIp;
    private String appName;
    private String appInstance;
    private String workEnv;
    private String workIdc;
    /**
     * 发送时间
     */
    private long time;
    private boolean format = true;

}
```

大家看到的消息内容体，主要体现在`content`和`stack`和`time`三个字段。

其他的字段，主要用于统计分析和帮助定位问题使用。

现在，框架发送的消息，都会区分`module`，详细见`com.ciicgat.grus.performance.Module`，主要有`redis`、`web-servlet`、`httpclient`、`feign`、`db`、`rabbitmq`。而一般的，开发在业务代码里发送的消息没有指定`module`字段。

如果想指定，可以通过以下方法

```
 public abstract class FrigateNotifier implements FrigateMessageConstants {
     /**
     * 指定模块发送消息
     * @param channel
     * @param module 模块
     * @param content
     * @param throwable
     */
 public static void sendMessageByAppName(NotifyChannel channel, Module module, String content, Throwable throwable) {
 .....
 }
 }
```

## 屏蔽消息

现在框架会发送很多消息给开发，有时候，大家希望能够屏蔽某些已知的非重要消息。大家可以按照本文的介绍做。

首先屏蔽消息，只是代表大家通过企业微信、短信、邮件收不到消息，消息本身会还是会记录到 elasticsearch 之中，供大家查询。

大家在自己应用名所对应的 gconf app 中（统一在上海 idc 下、即使你应用部署在 ali 生产也在上海 idc 下），创建`message-exclude.properties`的配置：

```
#excludeAll为true表示屏蔽该应用所有消息
excludeAll=false

#前缀0、1、2、3。。。用来聚合一组配置项，大家可以任意添加
#module表示屏蔽的模块，为空时表示空的模块。优先看这个。
#contentKey表示内容关键词，只要包含该值，消息就不发送。填*表示泛匹配，一律不发送。
#stackKey表示堆栈异常关键词，只要包含该值，消息就不发送。填*表示泛匹配，一律不发送。
#contentKey和stackKey之间是或关系，满足任意一个，消息就不发送

#以下配置表示，当前应用，redis模块相关的所有消息都屏蔽
0.module=redis
0.contentKey=*
0.stackKey=*

#以下配置表示，当前应用，feign模块包含EnterpriseLimitService的内容都屏蔽
1.module=feign
1.contentKey=EnterpriseLimitService
1.stackKey=

#以下配置表示，当前应用，空模块包含ClientAbortException异常的都屏蔽
3.module=
3.contentKey=
3.stackKey=ClientAbortException

。。。。
```
