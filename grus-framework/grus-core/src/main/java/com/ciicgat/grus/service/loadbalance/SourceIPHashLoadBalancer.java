/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.loadbalance;

import com.ciicgat.sdk.util.system.Systems;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * 源IP hash
 *
 * @Author: August
 * @Date: 2021/7/12 13:09
 */
public class SourceIPHashLoadBalancer extends LoadBalancer {

    SourceIPHashLoadBalancer() {
        super("sh");
    }

    /**
     * 采用一致性hash算法
     *
     * @param bound
     * @return
     */
    @Override
    protected int doSelectIndex(int bound) {
        return Hashing.consistentHash(HashCode.fromString(Systems.HOST_IP), bound);
    }
}
