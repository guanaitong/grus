/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2020-04-07 18:11.
 */
public class MaskerTest {

    @Test
    public void maskName() {
        Assertions.assertEquals(Masker.maskName(""), "");
        Assertions.assertEquals(Masker.maskName(null), null);
        Assertions.assertEquals(Masker.maskName("孙长浩"), "孙*浩");
        Assertions.assertEquals(Masker.maskName("陈涛"), "陈*");
        Assertions.assertEquals(Masker.maskName("钱"), "*");
        Assertions.assertEquals(Masker.maskName("august"), "a*g*s*");
    }

    @Test
    public void maskPhoneNum() {
        Assertions.assertEquals("", Masker.maskPhoneNum(""));
        Assertions.assertEquals(null, Masker.maskPhoneNum(null));
        Assertions.assertEquals("131****9288", Masker.maskPhoneNum("13167019288"));
        Assertions.assertEquals("8613167019288", Masker.maskPhoneNum("8613167019288"));
    }

    @Test
    public void maskEmail() {
        Assertions.assertEquals("", Masker.maskEmail(""));
        Assertions.assertEquals(null, Masker.maskEmail(null));
        Assertions.assertEquals("au*******ou@guanaitong.com", Masker.maskEmail("august.zhou@guanaitong.com"));
        Assertions.assertEquals("au**1@guanaitong.com", Masker.maskEmail("au111@guanaitong.com"));
        Assertions.assertEquals("a*@guanaitong.com", Masker.maskEmail("au@guanaitong.com"));
    }

    @Test
    public void maskIdNum() {
        Assertions.assertEquals(Masker.maskIdNum(""), "");
        Assertions.assertEquals(Masker.maskIdNum(null), null);
        Assertions.assertEquals(Masker.maskIdNum("421302199208165464"), "4213**********5464");
        Assertions.assertEquals(Masker.maskIdNum("42130219920816546x"), "4213**********546x");
    }
}
