/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.idgen;

import com.ciicgat.grus.zk.TestZkConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/27 16:41
 * @Description:
 */
public class ZKWorkIdHolderTest {
    @Test
    public void testFetchId() throws Exception {
        Long lastId = null;
        for (int i = 0; i < 16; i++) {
            ZKWorkIdHolder zkWorkIdHolder = new ZKWorkIdHolder(TestZkConfig.ZK, "payment");
            long workId = zkWorkIdHolder.getId(16);

            if (lastId != null && workId != 0) {
                Assert.assertEquals(1, workId - lastId);
            }
            lastId = workId;
        }
    }
}
