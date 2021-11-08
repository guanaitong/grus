/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.idgen;

import com.ciicgat.grus.zk.TestZkConfig;
import com.ciicgat.grus.zk.ZKUtils;
import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/27 16:41
 * @Description:
 */
public class ZKWorkIdHolderTest {
    @Test
    public void testFetchId() throws Exception {
        CuratorFramework curatorFramework = ZKUtils.init(TestZkConfig.ZK);
        Set<Long> ids = new HashSet<>();
        var n = 100;
        for (int i = 0; i < n; i++) {
            ZKWorkIdHolder zkWorkIdHolder = new ZKWorkIdHolder(curatorFramework, "payment");
            long workId = zkWorkIdHolder.getId();
            ids.add(workId);
        }
        Assertions.assertEquals(n, ids.size());
    }
}
