/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2018-10-22 10:14.
 */
public class TestErrorCode {

    @Test
    public void test1() {

        BaseErrorCode baseErrorCode = new BaseErrorCode(12312938, "xxxx");

        BaseErrorCode baseErrorCode1 = new BaseErrorCode(12312938, "xxxx");

        Assert.assertEquals(baseErrorCode, baseErrorCode1);

        Assert.assertEquals(baseErrorCode.hashCode(), baseErrorCode1.hashCode());

        Assert.assertEquals(baseErrorCode.toString(), baseErrorCode1.toString());
    }


    @Test
    public void test2() {

        int i = 1231111;
        BaseErrorCode baseErrorCode = new BaseErrorCode(i, "yyyyyyyyyy");

        Assert.assertSame(baseErrorCode, ErrorCenter.valueOf(i));

        Assert.assertNull(ErrorCenter.valueOf(1111));
    }

    @Test
    public void testStandardErrorCode3() {
        StandardErrorCode standardErrorCode = new StandardErrorCode(1002, 5, 99999, "xx异常");

        Assert.assertEquals("1002599999", standardErrorCode.getErrorCode() + "");

        Assert.assertEquals(1002, standardErrorCode.getFirstCode());
        Assert.assertEquals(5, standardErrorCode.getSecondCode());
        Assert.assertEquals(99999, standardErrorCode.getThirdCode());
    }



    @Test(expected = IllegalArgumentException.class)
    public void testStandardErrorCode4() {
        new StandardErrorCode(999, 5, 99999, "xx异常");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testStandardErrorCode5() {
        new StandardErrorCode(10000, 5, 99999, "xx异常");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testStandardErrorCode6() {
        new StandardErrorCode(1002, 0, 99999, "xx异常");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStandardErrorCode7() {
        new StandardErrorCode(1002, 10, 99999, "xx异常");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStandardErrorCode8() {
        new StandardErrorCode(1002, 5, -1, "xx异常");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStandardErrorCode9() {
        new StandardErrorCode(1002, 5, 100000, "xx异常");
    }

    @Test
    public void testStandardErrorCode10() {
        StandardErrorCode standardErrorCode = new StandardErrorCode(2146, 9, 99999, "xx异常");

        Assert.assertEquals("2146999999", standardErrorCode.getErrorCode() + "");

        Assert.assertEquals(2146, standardErrorCode.getFirstCode());
        Assert.assertEquals(9, standardErrorCode.getSecondCode());
        Assert.assertEquals(99999, standardErrorCode.getThirdCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStandardErrorCode11() {
        new StandardErrorCode(2147, 0, 0, "xx异常");
    }
}
