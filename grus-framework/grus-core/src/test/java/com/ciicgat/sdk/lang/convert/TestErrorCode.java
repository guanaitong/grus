/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2018-10-22 10:14.
 */
public class TestErrorCode {

    @Test
    public void test1() {

        BaseErrorCode baseErrorCode = new BaseErrorCode(12312938, "xxxx");

        BaseErrorCode baseErrorCode1 = new BaseErrorCode(12312938, "xxxx");

        Assertions.assertEquals(baseErrorCode, baseErrorCode1);

        Assertions.assertEquals(baseErrorCode.hashCode(), baseErrorCode1.hashCode());

        Assertions.assertEquals(baseErrorCode.toString(), baseErrorCode1.toString());
    }


    @Test
    public void test2() {

        int i = 1231111;
        BaseErrorCode baseErrorCode = new BaseErrorCode(i, "yyyyyyyyyy");

        Assertions.assertSame(baseErrorCode, ErrorCenter.valueOf(i));

        Assertions.assertNull(ErrorCenter.valueOf(1111));
    }

    @Test
    public void testStandardErrorCode3() {
        StandardErrorCode standardErrorCode = new StandardErrorCode(1002, 5, 99999, "xx异常");

        Assertions.assertEquals("1002599999", standardErrorCode.getErrorCode() + "");

        Assertions.assertEquals(1002, standardErrorCode.getFirstCode());
        Assertions.assertEquals(5, standardErrorCode.getSecondCode());
        Assertions.assertEquals(99999, standardErrorCode.getThirdCode());
    }


    @Test
    public void testStandardErrorCode4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StandardErrorCode(999, 5, 99999, "xx异常"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StandardErrorCode(10000, 5, 99999, "xx异常"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StandardErrorCode(1002, 0, 99999, "xx异常"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StandardErrorCode(1002, 10, 99999, "xx异常"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StandardErrorCode(1002, 5, -1, "xx异常"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StandardErrorCode(1002, 5, 100000, "xx异常"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StandardErrorCode(2147, 0, 0, "xx异常"));
    }



    @Test
    public void testStandardErrorCode10() {
        StandardErrorCode standardErrorCode = new StandardErrorCode(2146, 9, 99999, "xx异常");

        Assertions.assertEquals("2146999999", standardErrorCode.getErrorCode() + "");

        Assertions.assertEquals(2146, standardErrorCode.getFirstCode());
        Assertions.assertEquals(9, standardErrorCode.getSecondCode());
        Assertions.assertEquals(99999, standardErrorCode.getThirdCode());
    }

}
