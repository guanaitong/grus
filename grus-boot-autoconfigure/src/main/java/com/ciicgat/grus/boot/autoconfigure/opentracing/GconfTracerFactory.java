/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentracing;

import com.ciicgat.grus.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.lang.tool.PropertiesUtils;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkRegion;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.internal.samplers.RateLimitingSampler;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerFactory;
import io.opentracing.noop.NoopTracerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by August.Zhou on 2018/8/24 9:41.
 */
public class GconfTracerFactory implements TracerFactory {
    static final String LOCAL_HOST_IP = "127.0.0.1";
    private static final Logger LOGGER = LoggerFactory.getLogger(GconfTracerFactory.class);

    public GconfTracerFactory() {

    }

    private static String getGateWayIp() {
        String ip = Systems.HOST_IP;
        String[] ipSeg = ip.split("\\.");
        ipSeg[3] = "1";
        return String.join(".", ipSeg);
    }

    @Override
    public Tracer getTracer() {
        Confs confs = GlobalGconfConfig.getConfig().getBean("jaeger.properties", content -> getConfs(content));
        if (confs == null) {
            return NoopTracerFactory.create();
        }

        return confs.getTracer();
    }

    private Confs getConfs(String content) {
        Properties properties = PropertiesUtils.readFromText(content);
        String host = Systems.IN_K8S ? getGateWayIp() : properties.getProperty("jaeger.agent.host", LOCAL_HOST_IP);
        LOGGER.info("host {}", host);

        //上报Agent配置
        Configuration.SenderConfiguration senderConfiguration =
                new Configuration
                        .SenderConfiguration()
                        .withAgentHost(host)
                        .withAgentPort(Integer.valueOf(properties.getProperty("jaeger.agent.port", "6831")));

        Configuration.ReporterConfiguration reporterConfig =
                new Configuration
                        .ReporterConfiguration()
                        .withSender(senderConfiguration)
                        .withLogSpans(Boolean.FALSE);

        Configuration.SamplerConfiguration samplerConfig = null;
        if (WorkRegion.getCurrentWorkRegion().isDevelop()) {
            //对于那些unknown的不统计
            Float parm = "unknown".equals(Systems.APP_NAME) ? 0f : 1f;
            //采样配置
            samplerConfig =
                    new Configuration
                            .SamplerConfiguration()
                            .withType(ConstSampler.TYPE)
                            .withParam(parm);
        } else {
            Float parm = "unknown".equals(Systems.APP_NAME) ? 0 : Float.parseFloat(properties.getProperty("jaeger.sample.parm", "50"));
            //采样配置
            samplerConfig =
                    new Configuration
                            .SamplerConfiguration()
                            .withType(RateLimitingSampler.TYPE)
                            .withParam(parm);
        }

        return new Confs(samplerConfig, reporterConfig);

    }

    private static class Confs {
        final Configuration.SamplerConfiguration samplerConfig;
        final Configuration.ReporterConfiguration reporterConfig;

        Confs(Configuration.SamplerConfiguration samplerConfig, Configuration.ReporterConfiguration reporterConfig) {
            this.samplerConfig = samplerConfig;
            this.reporterConfig = reporterConfig;
        }

        public Tracer getTracer() {
            try {
                return new Configuration(Systems.APP_NAME).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
            } catch (Exception e) {
                LOGGER.error("error", e);
                return NoopTracerFactory.create();
            }
        }

    }
}
