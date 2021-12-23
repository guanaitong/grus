/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentelemetry;

import com.ciicgat.grus.boot.autoconfigure.condition.ConditionalOnServerEnv;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-03-25 14:55.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({OpenTelemetrySdk.class, LoggingSpanExporter.class})
@ConditionalOnProperty(prefix = "grus.opentelemetry", value = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnServerEnv
public class OpenTelemetryAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryAutoConfiguration.class);

    @Bean(destroyMethod = "close")
    public SdkTracerProvider sdkTracerProvider() {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().addSpanProcessor(SimpleSpanProcessor.create(new LoggingSpanExporter())).build();
        return sdkTracerProvider;
    }

    @Bean
    public OpenTelemetry openTelemetry(SdkTracerProvider sdkTracerProvider) {
        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance());
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();
        GlobalOpenTelemetry.set(sdk);
        return sdk;
    }


}
