/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.grus.rabbitmq.Exchange;
import com.ciicgat.grus.rabbitmq.Queue;
import com.ciicgat.grus.rabbitmq.QueueBind;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by August.Zhou on 2022/7/7 12:20
 */
@ConfigurationProperties(prefix = "grus.rabbitmq")
public class RabbitMQProperty {

    /**
     * 地址，支持集群。格式：192.168.1.1:5672,192.168.1.2:5672
     */
    private String address;

    /**
     * 用户，为空时使用全局配置
     */
    private String user;
    /**
     * 密码，为空时使用全局配置
     */
    private String pwd;
    /**
     * 虚拟主机
     */
    private String vhost = "/";

    /**
     * 生产者和消费者是否共用同一个连接
     */
    private boolean useSingleConnection = true;
    /**
     * 生产者配置
     */
    private Producer producer = new Producer();

    /**
     * 是否启用消费者
     */
    private Consumer consumer = new Consumer();

    /**
     * 用于exchange、queue的初始化、queue的绑定
     */
    private Init init = new Init();

    @PostConstruct
    private void init() {
        Properties properties = GlobalGconfConfig.getConfig().getProperties("rabbitmq.properties");
        if (!StringUtils.hasLength(user)) {
            user = properties.getProperty("mq.default.username");
        }
        if (!StringUtils.hasLength(pwd)) {
            pwd = properties.getProperty("mq.default.pwd");
        }
        if (!StringUtils.hasLength(address)) {
            if (properties.containsKey("mq.cluster.address")) {
                address = properties.getProperty("mq.cluster.address");
            } else if (properties.containsKey("mq.ha.address")) {
                address = properties.getProperty("mq.ha.address");
            } else {
                address = "mq." + WorkRegion.getCurrentWorkRegion().getServerDomainSuffix() + ":5672";
            }
        }
        if (Systems.IN_K8S && !WorkRegion.getCurrentWorkRegion().isProduct()) {
            useSingleConnection = true;
        }
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Init getInit() {
        return init;
    }

    public void setInit(Init init) {
        this.init = init;
    }

    public boolean isUseSingleConnection() {
        return useSingleConnection;
    }

    public void setUseSingleConnection(boolean useSingleConnection) {
        this.useSingleConnection = useSingleConnection;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public static class Producer {
        /**
         * 是否启用生产者
         */
        private boolean enable = false;
        /**
         * 是否启用confirm
         */
        private boolean confirm = false;

        /**
         * 是否生成消息Id，默认为true
         */
        private boolean createMessageId = true;
        /**
         * 是否生成消息时间戳，默认为true
         */
        private boolean createTimestamp = true;
        /**
         * 最大并发数
         */
        private int parallel = 4;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public boolean isConfirm() {
            return confirm;
        }

        public void setConfirm(boolean confirm) {
            this.confirm = confirm;
        }

        public int getParallel() {
            return parallel;
        }

        public void setParallel(int parallel) {
            this.parallel = parallel;
        }

        public boolean isCreateMessageId() {
            return createMessageId;
        }

        public void setCreateMessageId(boolean createMessageId) {
            this.createMessageId = createMessageId;
        }

        public boolean isCreateTimestamp() {
            return createTimestamp;
        }

        public void setCreateTimestamp(boolean createTimestamp) {
            this.createTimestamp = createTimestamp;
        }

    }

    public static class Consumer {
        /**
         * 是否启用消费者
         */
        private boolean enable;

        /**
         * 最大并发数
         */
        private int parallel = 4;

        /**
         * 设置预取的个数。当parallelNum设置比较大的时候，prefetchCount不易过大。
         * 也就是说，parallelNum*prefetchCount的值不易过大。
         * 当发生消息堆积时，当前queue的unack的消息个数一般等于parallelNum*prefetchCount。
         * 如果过大，那么unack的消息数量也过大，此时会影响mq性能。另外，如果用到优先级队列，那么因为当前客户端预取的过多，优先级队列实际也不会生效。
         */
        private int prefetch = 3;

        /**
         * 最大重试次数，默认5
         */
        private int maxRetry = 5;

        /**
         * 当消息abort时，是否去全局错误队列
         */
        private boolean fallBack = true;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getParallel() {
            return parallel;
        }

        public void setParallel(int parallel) {
            this.parallel = parallel;
        }

        public int getPrefetch() {
            return prefetch;
        }

        public void setPrefetch(int prefetch) {
            this.prefetch = prefetch;
        }

        public int getMaxRetry() {
            return maxRetry;
        }

        public void setMaxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        public boolean isFallBack() {
            return fallBack;
        }

        public void setFallBack(boolean fallBack) {
            this.fallBack = fallBack;
        }

    }

    public static class Init {
        /**
         * 是否启用初始化
         */
        private boolean enable;

        private List<Exchange> exchanges = new ArrayList<>();

        private List<Queue> queues = new ArrayList<>();

        private List<QueueBind> binds = new ArrayList<>();

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public List<Exchange> getExchanges() {
            return exchanges;
        }

        public void setExchanges(List<Exchange> exchanges) {
            this.exchanges = exchanges;
        }

        public List<Queue> getQueues() {
            return queues;
        }

        public void setQueues(List<Queue> queues) {
            this.queues = queues;
        }

        public List<QueueBind> getBinds() {
            return binds;
        }

        public void setBinds(List<QueueBind> binds) {
            this.binds = binds;
        }
    }
}
