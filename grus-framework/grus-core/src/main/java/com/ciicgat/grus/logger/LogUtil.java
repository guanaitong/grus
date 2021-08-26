/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.logger;

/**
 * @author Wei Jiaju
 * @date 2020/4/26 17:49
 */
public class LogUtil {

    private LogUtil() {
        // hide construct
    }

    private static final Integer MAX_LENGTH = 2000;

    public static String truncate(String text) {
        if (text == null) {
            return null;
        }
        if (text.length() <= MAX_LENGTH) {
            return text;
        }

        return text.substring(0, MAX_LENGTH);
    }

    public static boolean checkPrintReq(LogExclude logExclude) {
        return logExclude == null || !logExclude.excludeReq();
    }

    public static boolean checkPrintResp(LogExclude logExclude) {
        return logExclude == null || !logExclude.excludeResp();
    }

}
