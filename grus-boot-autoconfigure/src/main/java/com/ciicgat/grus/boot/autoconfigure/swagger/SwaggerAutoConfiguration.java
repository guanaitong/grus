/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.swagger;

import com.ciicgat.grus.boot.autoconfigure.condition.ConditionalOnWorkEnv;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkEnv;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import springfox.boot.starter.autoconfigure.OpenApiAutoConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

/**
 * 启用API的标准文档输出。
 * <p>
 * 是否启用，依赖于环境和 grus.swagger.enabled 配置，具体见 {@link com.ciicgat.grus.boot.autoconfigure.gconf.GconfContextInitializer#initSwagger(ConfigurableEnvironment)}
 *
 * @author Albert
 * @author Wei Jiaju
 * @author Stanley Shen
 * @date 2020/10/15
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SwaggerProperty.class)
@ConditionalOnClass(Docket.class)
@ConditionalOnProperty(prefix = "grus.swagger", value = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnWorkEnv({WorkEnv.DEVELOP, WorkEnv.TEST})
@AutoConfigureBefore(OpenApiAutoConfiguration.class)
public class SwaggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Docket docket(SwaggerProperty swaggerProperty) {
        checkValid(swaggerProperty);
        return new Docket(DocumentationType.OAS_30)
                .enable(true)
                .apiInfo(apiInfo(swaggerProperty))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperty.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }

    @Bean
    @ConditionalOnMissingBean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .showCommonExtensions(Boolean.TRUE)
                .build();
    }

    private void checkValid(SwaggerProperty swaggerProperty) {
        if (swaggerProperty.getBasePackage() == null) {
            Class<?> mainApplicationClass = deduceMainApplicationClass();
            if (mainApplicationClass != null) {
                swaggerProperty.setBasePackage(mainApplicationClass.getPackage().getName());
            }
        }
        if (swaggerProperty.getTitle() == null) {
            swaggerProperty.setTitle(Systems.APP_NAME + " Api Doc");
        }
        if (swaggerProperty.getVersion() == null) {
            swaggerProperty.setVersion("1.0.0");
        }
    }

    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

    private ApiInfo apiInfo(SwaggerProperty swaggerProperty) {
        return new ApiInfoBuilder()
                .title(swaggerProperty.getTitle())
                .description(swaggerProperty.getDescription())
                .termsOfServiceUrl(swaggerProperty.getTermsOfServiceUrl())
                .contact(new Contact(swaggerProperty.getContactName(), swaggerProperty.getContactUrl(), swaggerProperty.getContactEmail()))
                .license(swaggerProperty.getLicense())
                .licenseUrl(swaggerProperty.getLicenseUrl())
                .version(swaggerProperty.getVersion())
                .build();
    }

}
