/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.ciicgat.grus.alert.Alert;
import org.junit.After;
import org.junit.Test;

import java.util.Date;
import java.util.Objects;

/**
 * Created by August.Zhou on 2021/9/27 13:40.
 */
public class AlertTest {

    @After
    public void tearDown() throws Exception {
        try {
            if (Objects.isNull(System.getenv("CI"))) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void sendMsg() {
        MsgClient.setSkip(false);
        GroupBot.GLOBAL_GROUP_KEY = "ceed6aa3-ca81-4e9e-9638-9bc23e115a16";
        Alert.send("构建知会1：" + new Date());
        Alert.send("构建知会2：" + new Date(), new RuntimeException("ex"));
    }
}
