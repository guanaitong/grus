/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.util.frigate.FrigateClient;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import org.junit.Test;

import java.util.Objects;

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
        ConfigCollection config = GlobalGconfConfig.getConfig();
        FrigateClient.setSkip(false);
        FrigateNotifier.sendMessage(NotifyChannel.ALL, HUGE_CONTENT, new RuntimeException("都错了"), "HB533", "HB266");
        if (Objects.isNull(System.getenv("CI"))) {
            Threads.sleepSeconds(3);
        }
    }

}
