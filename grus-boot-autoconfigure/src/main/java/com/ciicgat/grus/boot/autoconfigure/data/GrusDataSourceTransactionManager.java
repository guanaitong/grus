/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

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
    private static final String WARN_MSG = "transaction rollback happened";

    public GrusDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        LOGGER.warn(WARN_MSG);
        super.doRollback(status);
    }

}
