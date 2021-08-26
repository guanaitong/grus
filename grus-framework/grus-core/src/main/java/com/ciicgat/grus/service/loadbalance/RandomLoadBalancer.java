/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.loadbalance;

import java.util.Random;

/**
 * @Author: August
 * @Date: 2021/7/12 12:53
 */
public class RandomLoadBalancer extends LoadBalancer {
    private final Random random = new Random();

    RandomLoadBalancer() {
        super("random");
    }

    @Override
    protected int doSelectIndex(int bound) {
        return random.nextInt(bound);
    }
}
