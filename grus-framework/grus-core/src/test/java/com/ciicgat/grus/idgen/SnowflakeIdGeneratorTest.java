/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.idgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/18 15:39
 * @Description: 基于雪花算法的序列生成器，字符串类型，序列长度28位
 */
public class SnowflakeIdGeneratorTest {

    private class FakeWorkIdHolder implements WorkIdHolder {
        @Override
        public long getId() {
            return new Random().nextInt(128);
        }

    }

    @Test
    public void testGenNo() {
        FakeWorkIdHolder fakeWorkIdHolder = new FakeWorkIdHolder();
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(fakeWorkIdHolder, "yyyyMMdd");

        Set<String> set = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            String orderNo = generator.makeNo();
            Assertions.assertEquals(24, orderNo.length());
            set.add(orderNo);
        }
        Assertions.assertEquals(10000, set.size());
    }

    @Test
    public void testGenId() {
        FakeWorkIdHolder fakeWorkIdHolder = new FakeWorkIdHolder();
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(fakeWorkIdHolder, "yyyyMMdd");

        Set<Long> set = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            Long id = generator.makeId();
            set.add(id);
        }
        Assertions.assertEquals(10000, set.size());
    }
}
