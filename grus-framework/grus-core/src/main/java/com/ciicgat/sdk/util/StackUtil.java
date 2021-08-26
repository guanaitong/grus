/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by August.Zhou on 2019-09-18 17:19.
 */
public class StackUtil {

    public static String getStack(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
