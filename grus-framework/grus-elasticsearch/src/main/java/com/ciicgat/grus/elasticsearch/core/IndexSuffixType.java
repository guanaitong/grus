/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by August.Zhou on 2019-09-09 11:20.
 */
public enum IndexSuffixType {
    /**
     * 不使用后缀
     */
    NONE(null),

    /**
     * 每年
     */
    YEARLY("yyyy"),

    /**
     * 每月
     */
    MONTHLY("yyyy.MM"),

    /**
     * 每日
     */
    DAYILY("yyyy.MM.dd");

    private final String pattern;

    IndexSuffixType(String pattern) {
        this.pattern = pattern;
    }

    public String getCurrentIndex(String index) {
        if (pattern == null) {
            return index;
        }
        if (index.endsWith("-")) {
            return index + DateFormatUtils.format(System.currentTimeMillis(), pattern);
        }
        return index + "-" + DateFormatUtils.format(System.currentTimeMillis(), pattern);
    }

    public String getCurrentIndex(String index, Supplier<Date> timestampSupplier) {
        if (pattern == null) {
            return index;
        }
        if (index.endsWith("-")) {
            return index + DateFormatUtils.format(timestampSupplier.get(), pattern);
        }
        return index + "-" + DateFormatUtils.format(timestampSupplier.get(), pattern);
    }

    public String getCurrentIndex(String index, Date timestamp) {
        if (pattern == null) {
            return index;
        }
        if (index.endsWith("-")) {
            return index + DateFormatUtils.format(timestamp, pattern);
        }
        return index + "-" + DateFormatUtils.format(timestamp, pattern);
    }

    public String getNextIndex(String index) {
        if (pattern == null) {
            return index;
        }
        if (index.endsWith("-")) {
            return index + DateFormatUtils.format(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30), pattern);
        }
        return index + "-" + DateFormatUtils.format(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30), pattern);
    }

}
