/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.core;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.sdk.util.frigate.GroupBot;
import com.ciicgat.sdk.util.system.Systems;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Created by August.Zhou on 2019-07-05 10:16.
 */
@ConfigurationProperties(prefix = "grus.core")
@Validated
public class CoreProperties {

    /**
     * work_wechat group bot key,since version 2021.1.10+,we use work_wechat group bot to send system msg
     */
    @NotNull
    private String groupBotKey;


    private boolean metricBusinessException = true;

    private boolean metricSystemException = true;

    @PostConstruct
    private void init() {
        if (Objects.nonNull(groupBotKey)) {
            GroupBot.GLOBAL_GROUP_KEY = groupBotKey;
        }
        Alert.send(String.format("应用实例（%s）正在启动", Systems.APP_INSTANCE));
    }

    public String getGroupBotKey() {
        return groupBotKey;
    }

    public void setGroupBotKey(String groupBotKey) {
        this.groupBotKey = groupBotKey;
    }

    public boolean isMetricBusinessException() {
        return metricBusinessException;
    }

    public void setMetricBusinessException(boolean metricBusinessException) {
        this.metricBusinessException = metricBusinessException;
    }

    public boolean isMetricSystemException() {
        return metricSystemException;
    }

    public void setMetricSystemException(boolean metricSystemException) {
        this.metricSystemException = metricSystemException;
    }
}
