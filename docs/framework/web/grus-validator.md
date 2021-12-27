# validator 模块

请求参数校验。

## hibernate-validator（推荐使用）

`hibernate-validator` 在 `spring-boot` 中默认包含，使用起来也比较方便，支持级联校验（通过 `@javax.validation.Valid`），推荐使用。

### validator 基础

- 采用 `hibernate-validator` 时，需要捕获`ValidationException` 和 `BindException` 等等异常，如

  ```java
  @RestControllerAdvice
  @Component
  public class ExceptionHandler {

      @ExceptionHandler(ConstraintViolationException.class)
      public ApiResponse handleValidationException(ConstraintViolationException validationException) {
          Set<ConstraintViolation<?>> violations = validationException.getConstraintViolations();
          String errorMsg = "参数错误";
          for (ConstraintViolation<?> item : violations) {
              errorMsg = item.getMessage();
              LOGGER.error("请求参数校验错误,原因={}", item.getMessage());
          }
          return ApiResponse.fail(Errors.PARAMETER_ERROR.getErrorCode(), errorMsg);
      }

      @ExceptionHandler(BindException.class)
      public ApiResponse handleBindException(BindException e) {
          LOGGER.warn("BindException, param validate error: " + e.getMessage());
          return handleBindingResult(e.getBindingResult());
      }

      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ApiResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
          LOGGER.warn("MethodArgumentNotValidException, param validate error: " + e.getMessage());
          return handleBindingResult(e.getBindingResult());
      }
  }
  ```

  默认的提供的 `GlobalExceptionHandler` 已经处理了这些异常。

- 推荐大家使用 `@org.springframework.validation.annotation.Validated` 注解，可以加载 `Controller` 类上，对 RequestParam 生效。如果使用`@Valid`，是没有办法校验 RequestParam 的内容。同时，前者还可以对 springboot 配置文件生效，后者无法做到。

  示例如下：

  ```java
  @RestController
  @org.springframework.validation.annotation.Validated
  public class TestController {

      @GetMapping(name = "健康检查", value = "/test")
      @ResponseBody
      public ApiResponse<String> test(@NotEmpty(message="id不能为空")  String id) {
          return ApiResponse.success(id);
      }

   }
  ```

* 可以使用 BindResult 注入，自定义失败返回结果

* 网上的教程一大堆，推荐大家都看看。

  - <https://www.cnblogs.com/mr-yang-localhost/p/7812038.html>
  - <http://hibernate.org/validator/>

### grus 封装

- 需要设置错误码前缀，否则默认 1000，`grus.web.error-code-prefix=1001`

- `grus` 对其进行过简单封装，通过 `grus.hibernate.validator.enabled=true` 开启，默认开启快速失败，后续有可能添加更多支持

  ```properties
  # 是否开启，默认 false
  grus.hibernate.validator.enabled=true
  # 快速失败，默认为 true
  grus.hibernate.validator.fail-fast=true
  ```

* hibernate 默认返回的错误信息比较简单（未设置 message 的话），例如：不能为空，不能小于 1，没有字段信息。所以默认进行了一定处理：

  - 针对传入参数是对象，抛出的一般是 `BindException` 和 `MethodArgumentNotValidException`，会默认在 message 前面设置字段名称

    - 如果使用了 @NotEmpty(message="id 不能为空")，也会添加返回错误信息为“id id 不能为空”，请注意
    - 如果使用了 @NotEmpty，错误信息会返回 id 不能为空

  - 通过 `RequestParam` 校验的，不会添加前缀，请注意，有需要请主动填写 message

  - 针对 web 或者 bff，参数校验的返回一般有要求，不能进行默认处理，提供了配置关闭相关功能

    ```properties
    # 参数校验异常时, msg 前是否添加 fieldName，默认为 true，改成 false 可关闭
    grus.web.show-field-name-in-error=true
    ```

## 框架提供的几个通用 validator 注解

grus-core 中提供了以下注解：

- com.ciicgat.grus.validation.constraints.CheckHTMLTag
- com.ciicgat.grus.validation.constraints.DateStr
- com.ciicgat.grus.validation.constraints.Decimal
- com.ciicgat.grus.validation.constraints.EnumValue
- com.ciicgat.grus.validation.constraints.JsonStr

如果你觉得还有哪些场景的 validator 很通用，可以贡献进来。

## grus-validation 使用说明（不推荐使用）

`grus-boot-starter-validator` 不推荐大家使用，日后**可能考虑废弃**，建议使用 spring-boot-starter-web 中自带的 hibernate-validator 替代。老项目如正在使用 `grus-boot-starter-validator`，最好按情况逐渐替换。此处只是介绍下 grus-validation 的注意事项。

- application.properties 文件里，配置相关参数：

  ```properties
  # 设置参数错误的错误码（10位错误码的后5位），必填
  grus.validation.error-code=88888
  # 参数校验拦截的切面（可选，不建议修改），默认拦截RequestMapping，不会拦截 PostMapping
  grus.validation.point-cut=@annotation(org.springframework.web.bind.annotation.RequestMapping)
  ```

* 使用自定义的注解 @Max，@Min（`com.ciicgat.boot.validator.annotation` 包下，否则不生效）；

* 对象上添加自定义注解 @Valid，否则不生效；

* 不支持级联校验，即 A 对象里包含 B 对象，B 对象无法校验；

* 参数校验错误会 throw ValidateRuntimeException；

* 默认的，`GlobalExceptionHandler` 已经处理了这个异常。
