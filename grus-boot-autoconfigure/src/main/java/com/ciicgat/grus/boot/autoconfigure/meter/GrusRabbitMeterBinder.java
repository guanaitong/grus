/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.meter;

import com.ciicgat.sdk.mq.metrics.DelegateMetricsCollector;
import com.rabbitmq.client.impl.MicrometerMetricsCollector;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;

/**
 * Created by August.Zhou on 2019-12-09 15:49.
 */
public class GrusRabbitMeterBinder implements MeterBinder {
    @Override
    public void bindTo(MeterRegistry registry) {
        DelegateMetricsCollector.getConsumerInstance().setMetricsCollector(new MicrometerMetricsCollector(registry, "rabbitmq", Tags.of("name", "consumer")));
        DelegateMetricsCollector.getProducerInstance().setMetricsCollector(new MicrometerMetricsCollector(registry, "rabbitmq", Tags.of("name", "producer")));
    }
}
