# Grus 微服务流量控制

## 解决和适用的场景

随着微服务的流行，服务和服务之间的稳定性变得越来越重要。我们主要面向分布式服务架构的**轻量级**流量控制框架，主要以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度来帮助您保护服务的稳定性。

关爱通的服务，主要是两层：web 和 service。一般情况下，流量控制，主要用于 web 层。用于 web 层，主要有三个优点：

1. 可以直接把外部的流量限制或者挡住。一个 web 请求，到后端一般会放大 N 倍。拦截最外层的最有效。
2. 在流量阻塞的时候，可以直接返回给终端用户。假如是 service 层，那么还需要上面的调用和 UI 支持流量受限的返回错误类型，而且需要一直透传。
3. web 层可以做到用户级别(UV)的限流。

当然，也不是说 service 层的应用不能使用流量控制。它需要解决一下问题：

1. 一般的，一个核心服务，不能全局限流或者熔断。比如说会员所有 http 端点都熔断了，那么整个网站也就挂了。只能针对某个端点，比如`/getAddressById`
2. 遇到流量阻塞的时候，返回的结果，需要整个调用链路透传到最上层，然后最上层 UI 能够提示用户

本文会把规则制定下来。

我们主要基于阿里 Sentinel 来实现

## Sentinel

阿里巴巴 sentinel 是一个功能强大且轻量级的流量控制实现。大家可以[点击这里](https://github.com/alibaba/Sentinel/wiki/%E4%BB%8B%E7%BB%8D)查看介绍和文档。

由于内容过丰富，主要介绍我们将如何使用`sentinel`：

很简单，只需要增加 maven 依赖：

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
```

sentinel 核心是通过 filter 来达到流量控制的目的：

```
@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest sRequest = (HttpServletRequest) request;
        Entry entry = null;

        Entry methodEntry = null;

        try {
            String target = FilterUtil.filterTarget(sRequest);
            // Clean and unify the URL.
            // For REST APIs, you have to clean the URL (e.g. `/foo/1` and `/foo/2` -> `/foo/:id`), or
            // the amount of context and resources will exceed the threshold.
            UrlCleaner urlCleaner = WebCallbackManager.getUrlCleaner();
            if (urlCleaner != null) {
                target = urlCleaner.clean(target);
            }

            // Parse the request origin using registered origin parser.
            String origin = parseOrigin(sRequest);

            ContextUtil.enter(target, origin);
            entry = SphU.entry(target, EntryType.IN);


            // Add method specification if necessary
            if (httpMethodSpecify) {
                methodEntry = SphU.entry(sRequest.getMethod().toUpperCase() + COLON + target,
                        EntryType.IN);
            }

            chain.doFilter(request, response);
        } catch (BlockException e) { //当前服务触发流量控制
            HttpServletResponse sResponse = (HttpServletResponse) response;
            // Return the block page, or redirect to another URL.
            WebCallbackManager.getUrlBlockHandler().blocked(sRequest, sResponse, e); //渲染结果返回，这里可以自定义
        } catch (IOException e2) {
            Tracer.trace(e2); //用于降级异常的自动统计
            throw e2;
        } catch (ServletException e3) {
            Tracer.trace(e3); //用于降级异常的自动统计
            throw e3;
        } catch (RuntimeException e4) {
            Tracer.trace(e4); //用于降级异常的自动统计
            throw e4;
        } finally {
            if (methodEntry != null) {
                methodEntry.exit();
            }
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
    }
```

我们框架，实现了 UrlBlockHandler：

```
public class GrusUrlBlockHandler implements UrlBlockHandler {

    private static final String DEFAULT_BLOCK_MSG = ApiResponse.fail(ErrorCode.REQUEST_BLOCK).toString();

    private GrusRuntimeConfig grusRuntimeConfig = GrusFramework.getGrusRuntimeManager().getGrusRuntimeConfig();


    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        String blockPage = grusRuntimeConfig.getStringValue("grus.sentinel.web.servlet.block.page", null);
        if (blockPage == null) {
            response.addHeader("Content-Type", "application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print(DEFAULT_BLOCK_MSG);
            out.flush();
            out.close();
            return;
        }


        StringBuffer url = request.getRequestURL();
        if ("GET".equals(request.getMethod()) && StringUtil.isNotBlank(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }
        String redirectUrl = blockPage + "?http_referer=" + url.toString();
        // Redirect to the customized block page.
        response.sendRedirect(redirectUrl);

    }


}

其中：
 ErrorCode REQUEST_BLOCK = new BaseErrorCode(-1001, "当前访问量过大，请稍后重试!");
```

使用的时候，在 sentinel 的 dashborad 上，加上限流或者熔断规则时，默认会返回

```
{
	"code": -1001,
	"data": null,
	"msg": "当前访问量过大，请稍后重试!"
}
```

一般的，这个会直接透传的最上层 web。

如果需要页面返回，那么在自己应用的 gconf 中，增加`grus-config.properties`配置文件，添加`grus.sentinel.web.servlet.block.page`的配置即可，其值为需要 302 跳转的页面。
