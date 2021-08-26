/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentracing;

import com.ciicgat.sdk.util.system.Systems;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.RateLimitingSampler;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by August.Zhou on 2018/8/15 13:30.
 */
public class LocalTracerFactory implements TracerFactory {
    static final String LOCAL_HOST_IP = "127.0.0.1";
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTracerFactory.class);
    Configuration.SamplerConfiguration samplerConfig;
    Configuration.ReporterConfiguration reporterConfig;

    LocalTracerFactory() {
        String host = Systems.IN_K8S ? getGateWayIp() : LOCAL_HOST_IP;
        LOGGER.info("host {}", host);

        //上报Agent配置
        Configuration.SenderConfiguration senderConfiguration =
                new Configuration
                        .SenderConfiguration()
                        .withAgentHost(host)
                        .withAgentPort(6831);

        reporterConfig =
                new Configuration
                        .ReporterConfiguration()
                        .withSender(senderConfiguration)
                        .withLogSpans(false);

        Float parm = "unknown".equals(Systems.APP_NAME) ? 0f : 50f;
        //采样配置
        samplerConfig =
                new Configuration
                        .SamplerConfiguration()
                        .withType(RateLimitingSampler.TYPE)
                        .withParam(parm);

    }

    private static String getGateWayIp() {
        String ip = Systems.HOST_IP;
        String[] ipSeg = ip.split("\\.");
        ipSeg[3] = "1";
        return String.join(".", ipSeg);
    }

    @Override
    public Tracer getTracer() {
        JaegerTracer tracer = new Configuration(Systems.APP_NAME).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
        return tracer;
    }


}
