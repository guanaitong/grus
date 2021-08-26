/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;

/**
 * Created by August.Zhou on 2019-05-27 18:44.
 */
public class GrusDataSourceTransactionManager extends DataSourceTransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusDataSourceTransactionManager.class);

    public GrusDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder stringBuilder = new StringBuilder("rollback happened\n");

        for (StackTraceElement traceElement : stackTraceElements) {
            stringBuilder.append("\tat ").append(traceElement).append("\n");
        }

        var log = stringBuilder.toString();

        LOGGER.warn(log);
        FrigateNotifier.sendMessageByAppName(NotifyChannel.DEFAULT, Module.DB, log, null);


        super.doRollback(status);
    }

}
