/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;


/**
 * Created by August.Zhou on 2021/9/2 13:12.
 */
public class GrusWebLogPrinterTest {
    private final GrusWebLogPrinter grusWebLogPrinter = new GrusWebLogPrinter();
    private TestLogger testLogger = new TestLogger();
    private WebLogController webLogController;
    private WebLogController2 webLogController2;
    private WebLogController3 webLogController3;


    @Before
    public void setUp() throws Exception {
        grusWebLogPrinter.LOGGER = testLogger;
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new WebLogController());
        aspectJProxyFactory.addAspect(grusWebLogPrinter);
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        webLogController = (WebLogController) aopProxy.getProxy();

        AspectJProxyFactory aspectJProxyFactory2 = new AspectJProxyFactory(new WebLogController2());
        aspectJProxyFactory2.addAspect(grusWebLogPrinter);
        AopProxy aopProxy2 = proxyFactory.createAopProxy(aspectJProxyFactory2);
        webLogController2 = (WebLogController2) aopProxy2.getProxy();

        AspectJProxyFactory aspectJProxyFactory3 = new AspectJProxyFactory(new WebLogController3());
        aspectJProxyFactory3.addAspect(grusWebLogPrinter);
        AopProxy aopProxy3 = proxyFactory.createAopProxy(aspectJProxyFactory3);
        webLogController3 = (WebLogController3) aopProxy3.getProxy();
    }

    @Test
    public void test() throws Throwable {
        webLogController.isLive();
        webLogController.test("id", null, null);
        String msg = testLogger.getMsg();

        Assert.assertEquals(msg, "WEB_REQ METHOD: WebLogController.test PARAM: [\"id\"]\n" +
                "WEB_RSP METHOD: WebLogController.test RESULT: {\"code\":0,\"msg\":\"OK\",\"data\":\"I am OK ...\"}");
        webLogController.test1("id1", null);
        msg = testLogger.getMsg();
        Assert.assertEquals(msg, "WEB_REQ METHOD: WebLogController.test1 PARAM: [\"id1\",null]\n" +
                "WEB_RSP METHOD: WebLogController.test1 RESULT: {\"code\":0,\"msg\":\"OK\",\"data\":\"I am OK2 ...\"}");
        webLogController.test2("id1", "id2", "id3");
        msg = testLogger.getMsg();
        Assert.assertEquals(msg, "WEB_REQ METHOD: WebLogController.test2 PARAM: [\"id1\",\"id3\"]\n" +
                "WEB_RSP METHOD: WebLogController.test2 RESULT: null");
        webLogController.test3("id3");
        msg = testLogger.getMsg();
        Assert.assertEquals(msg, "WEB_REQ METHOD: WebLogController.test3 PARAM: [\"id3\"]\n" +
                "WEB_RSP METHOD: WebLogController.test3 RESULT: {\"code\":0,\"msg\":\"OK\",\"data\":\"I am OK3 ...\"}");
        webLogController.test4("id");
        msg = testLogger.getMsg();
        Assert.assertEquals(msg, "");
        try {
            webLogController.test5("id");
            Assert.fail();
        } catch (Exception e) {
            msg = testLogger.getMsg();
            Assert.assertEquals(msg, "WEB_EX METHOD: WebLogController.test5 ERROR");
        }


        webLogController2.test("id");
        msg = testLogger.getMsg();
        Assert.assertEquals(msg, "");
        webLogController3.test("id");
        msg = testLogger.getMsg();
        Assert.assertEquals(msg, "");
    }

}
