/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
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
     * 限制不支持TLSv1.2以下的安全协议
     */
    private boolean disableOldSslProtocol = true;

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

    /**
     * 单个接口的最大并发数
     */
    private int maxCurrencyOfSingleInterface = 50;

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

    public boolean isDisableOldSslProtocol() {
        return disableOldSslProtocol;
    }

    public void setDisableOldSslProtocol(boolean disableOldSslProtocol) {
        this.disableOldSslProtocol = disableOldSslProtocol;
    }

    public int getMaxCurrencyOfSingleInterface() {
        return maxCurrencyOfSingleInterface;
    }

    public void setMaxCurrencyOfSingleInterface(int maxCurrencyOfSingleInterface) {
        this.maxCurrencyOfSingleInterface = maxCurrencyOfSingleInterface;
    }
}
