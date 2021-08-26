/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

/**
 * Created by August.Zhou on 2019-06-26 12:40.
 */
public enum Level {

    L1(1),
    L2(2),
    L3(3),
    L4(4),
    L5(5),
    L6(6),
    L7(7),
    L8(8),
    L9(9),
    LH(Integer.MAX_VALUE);

    private final int value;

    Level(int value) {
        this.value = value;
    }


    public boolean biggerThan(Level o) {
        return this.value > o.value;
    }

}
