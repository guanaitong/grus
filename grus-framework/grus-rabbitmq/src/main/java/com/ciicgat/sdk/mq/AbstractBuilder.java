/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.util.system.WorkRegion;

import java.util.Properties;

/**
 * Created by August.Zhou on 2019-11-06 15:27.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBuilder<T, SELF extends AbstractBuilder> {

    private static final String DEFAULT_MQ_HOST = "mq." + WorkRegion.getCurrentWorkRegion().getServerDomainSuffix();

    private static final String DEFAULT_MQ_USERNAME;

    private static final String DEFAULT_MQ_PWD; //NOSONAR

    private static final String DEFAULT_VHOST = "/";

    private static final int DEFAULT_MQ_PORT = 5672;

    static {
        Properties properties = GlobalGconfConfig.getConfig().getProperties("rabbitmq.properties");
        DEFAULT_MQ_USERNAME = properties.getProperty("mq.default.username");
        DEFAULT_MQ_PWD = properties.getProperty("mq.default.pwd");
    }

    /**
     * mq host
     */
    String host = DEFAULT_MQ_HOST;

    /**
     * mq port
     */
    int port = DEFAULT_MQ_PORT;

    /**
     * user
     */
    String user = DEFAULT_MQ_USERNAME;

    /**
     * pwd
     */
    String pwd = DEFAULT_MQ_PWD; //NOSONAR

    /**
     * vhost
     */
    String vhost = DEFAULT_VHOST;


    String exchangeName;

    /**
     * 并发数，消费并发数或者发送并发数
     */
    int parallelNum = 2;

    /**
     * @param exchangeName
     * @return
     */
    public SELF setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
        return (SELF) this;
    }


    public SELF setHost(String host) {
        this.host = host;
        return (SELF) this;
    }


    public SELF setPort(int port) {
        this.port = port;
        return (SELF) this;
    }


    public SELF setUser(String user) {
        this.user = user;
        return (SELF) this;
    }


    public SELF setPwd(String pwd) {
        this.pwd = pwd;
        return (SELF) this;
    }

    public SELF setVhost(String vhost) {
        this.vhost = vhost;
        return (SELF) this;
    }

    public SELF setParallelNum(int parallelNum) {
        this.parallelNum = parallelNum;
        return (SELF) this;
    }


    public abstract T build() throws Exception;

}
