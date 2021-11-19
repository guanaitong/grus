---
title: Feign协议升级
---

## Feign contract

### 现状

```java
@ServiceName("user")
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface UserService {

    @RequestLine("POST /user/get")
    User getUser(@Param("id") Integer id);
}
```

```java
@RequestMapping("/user")
@RestController
public class UserController {

    @PostMapping("/get")
    ApiResponse<User1> getUser(GetUserRequest request){};
}
```

feign 的接口类 UserService，和服务端接口类 UserController 两者互相独立，虽然有灵活性的优点，但面对大量的业务接口时，大量的代码拷贝难以避免，阻碍开发效率的提升。

### Spring MVC Contract（2020.7.1.RELEASE）

通过引入 feign 的 spring mvc 协议可以解决重复代码的问题

```java
@ServiceName("user")
@RequestMapping("/user")
public interface UserService {

    @PostMapping("/get")
    User getUser(GetUserRequest request);
}
```

```java
@RestController
public class UserController implements UserService {

    @Override
    public User getUser(GetUserRequest request){};
}
```

服务端的接口类将直接基于 feign 的接口实现，关于接口路径，参数模型等的定义只需要写在 feign 接口中，即我们的巨灵神中即可。

1. 使用 Spring MVC Contract 的 controller 的对象返回值无需强制定义为 ApiResponse，接口的 code 和 msg 通过 HTTP 的 header 传递。
2. 默认的传参形式为 `application/x-www-form-urlencoded`，仍需**使用 `@UrlFormBody` 标注对象类**。

## 模型定义

由于服务端和巨灵神共用一个模型，一些参数校验注解和文档注解都需要在巨灵神的模型类上体现。

```java
@UrlFormBody
public class GetUserRequest {

  @NotNull
  @Min(0)
  @ApiModelProperty("用户ID")
  private Integer id;

  public Integer getId() { return id; }

  public void setId(Integer id) { this.id = id; }
}
```

```java
public class User {

  @ApiModelProperty("用户姓名")
  private String name;

  public String getName() { return name; }

  public void setName(String name) {this.name = name;}
}
```

良好的模型注解可以帮助开发对接接口，同时服务端的 swagger 上也会输出可读性强的文档，供其他开发测试人员使用。

## 最佳实践

后端 Service 层代码统一使用 post 接口，使用 `@PostMapping` 注解快速定义。优先选用 `application/x-www-form-urlencoded` 格式传输数据。优点是在 `swagger` 上生成的文档最为简洁和可读。

对于入参参数复杂，必须使用 `application/json` 定义时，需要在巨灵神中指定格式，并在服务或接口中（二选一）添加`@RequestBody` 注解。

```java
@ServiceName("user")
@RequestMapping("/user")
public interface UserService {

    @PostMapping(value = "/postJson", consumes = MediaType.APPLICATION_JSON_VALUE)
    User getUser(@RequestBody GetUserRequest request);

  	/**
  	 * 此处的 GetUserRequest 类上必须标注 @UrlFormBody，在巨灵神请求时特殊处理，不然 feign 默认是不支持 post+form 的
  	 */
    @PostMapping(value = "/postForm")
    User getUserByForm(GetUserRequest request);
}
```

```java
@RestController
public class UserController implements UserService {

    @Override
    public User getUser(GetUserRequest request){};
}
```

使用新的 MVC 协议后，服务端，客户端，巨灵神。三者的接口协议将完全一致。(原先只有客户端和巨灵神一致)。这在大部分情况下是我们所需要的，有些特殊场景可能需要做服务端客户端模型的差异化，例如服务对外暴露参数少于实际服务端接口。此时巨灵神接口依然可以使用 MVC 协议，但服务端可不继承巨灵神接口，而是使用继承模型方式复用代码。

```java
@RequestMapping("/user")
@RestController
public class UserController {

    @PostMapping("/get")
    User getUser(GetUserRequestEx request){};
}
```

```java
public class GetUserRequestEx {

  @NotNull
  @Min(0)
  @ApiModelProperty("员工工号")
  private String code;

  public String getCode() { return code; }

  public void setCode(String id) {this.code = code;}
}
```
