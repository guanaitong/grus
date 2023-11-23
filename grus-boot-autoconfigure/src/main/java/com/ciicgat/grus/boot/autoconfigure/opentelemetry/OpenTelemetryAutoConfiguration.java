/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentelemetry;

import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.sdk.util.system.Systems;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-03-25 14:55.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OpenTelemetryProperties.class})
public class OpenTelemetryAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryAutoConfiguration.class);

    @ConditionalOnMissingClass("io.opentelemetry.extension.trace.propagation.JaegerPropagator")
    public static class NoopConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OpenTelemetry openTelemetry() {
            OpenTelemetrys.set(OpenTelemetry.noop());
            return GlobalOpenTelemetry.get();
        }
    }

    @ConditionalOnClass({JaegerPropagator.class})
    public static class SdkConfiguration {
        private static final AttributeKey<String> SERVICE_NAME = AttributeKey.stringKey("service.name");
        private static final AttributeKey<String> SERVICE_INSTANCE_ID = AttributeKey.stringKey("service.instance.id");

        @Bean(destroyMethod = "close")
        @ConditionalOnMissingBean
        public SdkTracerProvider sdkTracerProvider(OpenTelemetryProperties openTelemetryProperties) {
            SdkTracerProviderBuilder sdkTracerProviderBuilder = SdkTracerProvider.builder()
                    .setResource(Resource.create(Attributes.builder()
                            .put(SERVICE_NAME, Systems.APP_NAME)
                            .put(SERVICE_INSTANCE_ID, Systems.APP_INSTANCE)
                            .build()));
            OpenTelemetryProperties.Collector collector = openTelemetryProperties.getCollector();
            if (collector.isEnable()) {
                OtlpGrpcSpanExporter grpcSpanExporter = OtlpGrpcSpanExporter.builder()
                        .setEndpoint(collector.getEndpoint())
                        .build();
                sdkTracerProviderBuilder.addSpanProcessor(BatchSpanProcessor.builder(grpcSpanExporter).build());
            }
            SdkTracerProvider sdkTracerProvider = sdkTracerProviderBuilder.build();
            return sdkTracerProvider;
        }

        @Bean
        @ConditionalOnMissingBean
        public OpenTelemetry openTelemetry(SdkTracerProvider sdkTracerProvider, OpenTelemetryProperties openTelemetryProperties) {
            TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance());
            OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();
            OpenTelemetrys.setPropagateWithNew(openTelemetryProperties.isPropagateWithNew());
            OpenTelemetrys.setPropagateUnSupportedMiddleware(OpenTelemetrys.isPropagateUnSupportedMiddleware());
            OpenTelemetrys.set(sdk);
            return sdk;
        }
    }


}
