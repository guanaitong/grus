# 分布式 SESSION

一般来说，对于登陆后的用户，我们可以使用`PassportAuth`封装的单点登录鉴权解决方案。

但是对于非登录用户，没有`GID`、没有`access-token`，比如`Passport`个人登录入口本身，那么就需要分布式 SESSION 了。

## 设计说明

session 产生流程图：

![session产生流程](../../assets/images/java/session.jpg)

## session 生命周期

1. session 的生命周期最长为 6 个小时
2. 如果用户不做任何操作，那么 1 个小时之后，session 自动过期

gid 对应的 session 的生命周期同上。

## session 生成限制

1. 单 IP 一小时最多生成 2 万个 session
2. 单“ip+useragent”一个小时最多生成 1 万个 session
3. 一小时最多生成 20 万个 session
4. 一分钟最多生成 1 万个 session

可以一定程度上防止 session 攻击。

## 使用说明

maven 依赖：

```xml
<dependency>
            <artifactId>ciicgat-sdk-session</artifactId>
            <groupId>com.ciicgat.sdk</groupId>
        </dependency>
```

```
HttpSession httpSession = SessionManager.getCurrentSession(req, resp);
httpSession.setAttribute("code","123456");
System.out.println(httpSession.getAttribute("code"));

注意:setAttribute方法中，value必须实现java.io.Serializable接口。
因为需要把其序列化到redis之中。
```
