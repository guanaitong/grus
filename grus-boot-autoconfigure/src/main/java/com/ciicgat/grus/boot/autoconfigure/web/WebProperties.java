/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by August.Zhou on 2019-05-29 11:23.
 */
@ConfigurationProperties(prefix = "grus.web")
public class WebProperties {

    /**
     * 错误码前缀，最大值不要超过2146
     */
    private int errorCodePrefix = 1000;

    /**
     * 参数校验异常时, msg 前是否添加 fieldName
     */
    private boolean showFieldNameInError = true;

    /**
     * 是否打印businessException异常堆栈
     */
    private boolean printBusinessErrorStack = false;

    public int getErrorCodePrefix() {
        return errorCodePrefix;
    }

    public void setErrorCodePrefix(int errorCodePrefix) {
        this.errorCodePrefix = errorCodePrefix;
    }

    public boolean isShowFieldNameInError() {
        return showFieldNameInError;
    }

    public void setShowFieldNameInError(boolean showFieldNameInError) {
        this.showFieldNameInError = showFieldNameInError;
    }

    public boolean isPrintBusinessErrorStack() {
        return printBusinessErrorStack;
    }

    public void setPrintBusinessErrorStack(boolean printBusinessErrorStack) {
        this.printBusinessErrorStack = printBusinessErrorStack;
    }
}
