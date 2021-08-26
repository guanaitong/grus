/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.idgen;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/30 17:40
 * @Description:
 */
public interface WorkIdHolder {
    long getId();

    long getId(long maxId);
}
