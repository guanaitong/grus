/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * Created by Albert on 2018/10/29.
 */
public class FrigateNotifierTest {

    @AfterEach
    public void tearDown() throws Exception {
        try {
            if (Objects.isNull(System.getenv("CI"))) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    String HUGE_CONTENT = "Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试" +
            "Albert测试Albert测试Albert测试Albert测试Albert测试Albert测试";

    @Test
    public void sendMessage() throws Exception {
        FrigateNotifier.sendMessage(NotifyChannel.ALL, HUGE_CONTENT, new RuntimeException("都错了"), "HB533", "HB266");
    }

    @BeforeAll
    public static void beforeClass() throws Exception {
        MsgClient.setSkip(false);
    }
}
