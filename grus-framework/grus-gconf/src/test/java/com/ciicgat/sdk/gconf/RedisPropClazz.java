/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.sdk.gconf.annotation.BeanFieldKey;
import org.junit.Assert;

/**
 * @Author: Jiaju.Wei
 * @Date: Created in 2018/6/21
 * @Description:
 */
public class RedisPropClazz implements GConfBeanValidator {

    @BeanFieldKey("host")
    String myHost;

    @BeanFieldKey("port")
    int myPort;

    @BeanFieldKey("ok")
    boolean ok;

    public String getMyHost() {
        return myHost;
    }

    public Integer getMyPort() {
        return myPort;
    }

    public boolean isOk() {
        return ok;
    }

    @Override
    public void validate() {
        Assert.assertNotNull(myHost);
        Assert.assertNotNull(myPort);
    }

    public Integer getNotBean() {
        return 1;
    }
}
