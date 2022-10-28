# 跨域

**跨域必须限制来源的域名是关爱通的域名**。

以下是`SpringBoot`的标准例子，其他语言或者技术栈类似：

```java
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        String suffix = WorkRegion.getCurrentWorkRegion().getPublicDomainSuffix();
        String[] allowedOriginPatterns = new String[]{
                "http://*.guanaitong." + suffix,
                "https://*.guanaitong." + suffix,
                "http://*.igeidao." + suffix,
                "https://*.igeidao." + suffix};
        // 如果是ciicgat，就换成 "http://*.ciicgat." + suffix
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowCredentials(true) //是否发送cookie
                        .allowedOriginPatterns(allowedOriginPatterns) //放行来源站点
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("*")
                        .maxAge(24 * 3600L);
            }
        };
    }
```



跨域时，浏览器会尝试先发起`OPTIONS`请求，看后端是否允许当前站点跨域请求目标站点，允许后才会发起真正的请求，不允许时，一般会有**403**返回码。

所以，我们可以使用curl命令测试跨域是否生效，而不需要写前端代码配合。

```bash
curl -H "Origin: https://a.x.guanaitong.dev" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: X-Requested-With" \
  -X OPTIONS --verbose \
  http://127.0.0.1:11082/isLive
  
  实际使用时，把Origin换成前端代码所属域名。http://127.0.0.1:11082/isLive2换成你本地的启动的接口地址。
```

返回403即代表失败，失败例子：

```bash
a.x.guanaitong.xx不在关爱通标准域名
~ curl -H "Origin: https://a.x.guanaitong.xx" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: X-Requested-With" \
  -X OPTIONS --verbose \
  http://127.0.0.1:11082/isLive2
*   Trying 127.0.0.1:11082...
* Connected to 127.0.0.1 (127.0.0.1) port 11082 (#0)
> OPTIONS /isLive2 HTTP/1.1
> Host: 127.0.0.1:11082
> User-Agent: curl/7.84.0
> Accept: */*
> Origin: https://a.x.guanaitong.xx
> Access-Control-Request-Method: POST
> Access-Control-Request-Headers: X-Requested-With
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 403
< x-app-name: grus-demo
< x-app-instance: unknown
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Transfer-Encoding: chunked
< Date: Fri, 28 Oct 2022 14:17:58 GMT
<
* Connection #0 to host 127.0.0.1 left intact
Invalid CORS request%
```

返回200同时返回头包含`Access-Control-Allow-Origin`且值为请求的Origin则代表成功：

```bash
a.x.guanaitong.dev是关爱通标准域名
~ curl -H "Origin: https://a.x.guanaitong.dev" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: X-Requested-With" \
  -X OPTIONS --verbose \
  http://127.0.0.1:11082/isLive
*   Trying 127.0.0.1:11082...
* Connected to 127.0.0.1 (127.0.0.1) port 11082 (#0)
> OPTIONS /isLive HTTP/1.1
> Host: 127.0.0.1:11082
> User-Agent: curl/7.84.0
> Accept: */*
> Origin: https://a.x.guanaitong.dev
> Access-Control-Request-Method: POST
> Access-Control-Request-Headers: X-Requested-With
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< x-app-name: grus-demo
< x-app-instance: unknown
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Access-Control-Allow-Origin: https://a.x.guanaitong.dev
< Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
< Access-Control-Allow-Headers: X-Requested-With
< Access-Control-Expose-Headers: *
< Access-Control-Allow-Credentials: true
< Access-Control-Max-Age: 86400
< Allow: GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH
< Content-Length: 0
< Date: Fri, 28 Oct 2022 14:16:53 GMT
```



