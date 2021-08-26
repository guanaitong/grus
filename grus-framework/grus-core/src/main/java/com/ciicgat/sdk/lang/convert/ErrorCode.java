/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

/**
 *
 */
package com.ciicgat.sdk.lang.convert;

import java.io.Serializable;

/**
 * 通用错误码枚举类型抽象接口<br>
 */
public interface ErrorCode extends Serializable {


    /**
     * 操作成功
     */
    ErrorCode SUCCESS = new BaseErrorCode(0, "OK");

    /**
     * 服务降级或者限流的时候，会使用该错误码
     */
    ErrorCode REQUEST_BLOCK = new BaseErrorCode(-1001, "当前访问量过大，请稍后重试!");


    /**
     * 获取全局错误码
     *
     * @return
     */
    int getErrorCode();

    /**
     * 获取错误信息描述
     *
     * @return
     */
    String getErrorMsg();

}
