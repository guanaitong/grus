/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.ciicgat.sdk.lang.threads.Threads;
import org.junit.Test;

/**
 * Created by Albert on 2018/10/29.
 */
public class FrigateNotifierTest {


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
        FrigateClient.setSkip(false);
        FrigateNotifier.sendMessage(NotifyChannel.ALL, HUGE_CONTENT, new RuntimeException("都错了"), "HB533", "HB266");
        Threads.sleepSeconds(3);
    }

}
