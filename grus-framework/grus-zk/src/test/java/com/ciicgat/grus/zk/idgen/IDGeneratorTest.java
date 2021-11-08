/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.idgen;

import com.ciicgat.grus.idgen.SnowflakeIdGenerator;
import com.ciicgat.grus.zk.TestZkConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/23 16:47
 * @Description:
 */
public class IDGeneratorTest {
    @Test
    public void testGenNo() throws Exception {
        ZKWorkIdHolder zkWorkIdHolder = new ZKWorkIdHolder(TestZkConfig.ZK, "payment");
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(zkWorkIdHolder, "yyyyMMdd");

        Set<String> set = new HashSet<>();
        for (int i = 0; i < 10240; i++) {
            String orderNo = generator.makeNo();
            Assertions.assertEquals(24, orderNo.length());
            set.add(orderNo);
        }
        Assertions.assertEquals(10240, set.size());
    }
}
