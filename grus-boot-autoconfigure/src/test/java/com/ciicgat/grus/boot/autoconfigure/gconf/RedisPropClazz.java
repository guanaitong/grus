/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import com.ciicgat.sdk.gconf.GConfBeanValidator;
import com.ciicgat.sdk.gconf.annotation.BeanFieldKey;
import org.springframework.util.Assert;

/**
 * Created by August.Zhou on 2019-04-01 18:35.
 */
@GConfBean(appId = "session", value = "redis.properties")
public class RedisPropClazz implements GConfBeanValidator {

    @BeanFieldKey("host")
    private String myHost;

    @BeanFieldKey("port")
    private int myPort;

    @BeanFieldKey("ok")
    private boolean ok;


    public RedisPropClazz() {
    }

    public String getMyHost() {
        return myHost;
    }

    public int getMyPort() {
        return myPort;
    }


    public boolean isOk() {
        return ok;
    }

    @Override
    public void validate() {
        Assert.notNull(myHost, "主机不能为空");
        Assert.notNull(myPort, "端口不能为空");
    }

    public Integer getNotBean() {
        return 1;
    }
}
