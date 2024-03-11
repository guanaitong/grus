/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.swagger;

import com.ciicgat.grus.boot.autoconfigure.condition.ConditionalOnWorkEnv;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkEnv;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

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
@ConditionalOnClass({OpenAPI.class, SpringDocConfiguration.class})
@ConditionalOnProperty(prefix = "grus.swagger", value = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnWorkEnv({WorkEnv.DEVELOP, WorkEnv.TEST})
@AutoConfigureBefore(SpringDocConfiguration.class)
public class SwaggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openAPI(SwaggerProperty swaggerProperty) {
        checkValid(swaggerProperty);
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info().title(swaggerProperty.getTitle())
                        .description(swaggerProperty.getDescription())
                        .version(swaggerProperty.getVersion())
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.txt")));
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

}
