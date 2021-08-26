/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.security;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2020-04-07 18:11.
 */
public class MaskerTest {

    @Test
    public void maskName() {
        Assert.assertEquals(Masker.maskName(""), "");
        Assert.assertEquals(Masker.maskName(null), null);
        Assert.assertEquals(Masker.maskName("孙长浩"), "孙*浩");
        Assert.assertEquals(Masker.maskName("陈涛"), "陈*");
        Assert.assertEquals(Masker.maskName("钱"), "*");
        Assert.assertEquals(Masker.maskName("august"), "a*g*s*");
    }

    @Test
    public void maskPhoneNum() {
        Assert.assertEquals("", Masker.maskPhoneNum(""));
        Assert.assertEquals(null, Masker.maskPhoneNum(null));
        Assert.assertEquals("131****9288", Masker.maskPhoneNum("13167019288"));
        Assert.assertEquals("8613167019288", Masker.maskPhoneNum("8613167019288"));
    }

    @Test
    public void maskEmail() {
        Assert.assertEquals("", Masker.maskEmail(""));
        Assert.assertEquals(null, Masker.maskEmail(null));
        Assert.assertEquals("au*******ou@guanaitong.com", Masker.maskEmail("august.zhou@guanaitong.com"));
        Assert.assertEquals("au**1@guanaitong.com", Masker.maskEmail("au111@guanaitong.com"));
        Assert.assertEquals("a*@guanaitong.com", Masker.maskEmail("au@guanaitong.com"));
    }

    @Test
    public void maskIdNum() {
        Assert.assertEquals(Masker.maskIdNum(""), "");
        Assert.assertEquals(Masker.maskIdNum(null), null);
        Assert.assertEquals(Masker.maskIdNum("421302199208165464"), "4213**********5464");
        Assert.assertEquals(Masker.maskIdNum("42130219920816546x"), "4213**********546x");
    }
}
