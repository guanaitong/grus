/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;


/**
 * Created by August.Zhou on 2017/4/27 19:32.
 */
public class StandardErrorCode extends BaseErrorCode {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 前四位表示业务系统代号
     */
    private int firstCode;
    /**
     * 第五位表示表示错误的类别
     */
    private int secondCode;

    /**
     * 后五位表示业务系统里的错误代码，由业务系统自行规划
     */
    private int thirdCode;


    public StandardErrorCode(int firstCode, int secondCode, int thirdCode, String errorMsg) {
        super(1000000 * firstCode + 100000 * secondCode + thirdCode, errorMsg);
        this.firstCode = setFirstCode(firstCode);
        this.secondCode = setSecondCode(secondCode);
        this.thirdCode = setThirdCode(thirdCode);
    }

    private int setFirstCode(int firstCode) {
        if (firstCode > 999 && firstCode < 2147) {
            this.firstCode = firstCode;
            return firstCode;
        }
        throw new IllegalArgumentException("firstCode: " + firstCode);
    }

    private int setSecondCode(int secondCode) {
        if (secondCode > 0 && secondCode < 10) {
            this.secondCode = secondCode;
            return secondCode;
        }
        throw new IllegalArgumentException("secondCode: " + secondCode);
    }

    private int setThirdCode(int thirdCode) {
        if (thirdCode > -1 && thirdCode < 100000) {
            this.thirdCode = thirdCode;
            return thirdCode;
        }
        throw new IllegalArgumentException("thirdCode: " + thirdCode);
    }

    public int getFirstCode() {
        return firstCode;
    }

    public int getSecondCode() {
        return secondCode;
    }

    public int getThirdCode() {
        return thirdCode;
    }
}
