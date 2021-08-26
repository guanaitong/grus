/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.lock;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 10:31
 * @Description: 锁生成工厂接口
 */
public interface DistLockFactory {
    DistLock buildLock(String keyName);
}
