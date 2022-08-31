/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.alert;

public enum AlertLevel {
    NORMAL("", ""),
    IMPORTANT("重要", "yellow"),
    URGE("紧急", "red");


    final String title;

    final String color;

    AlertLevel(String title, String color) {
        this.title = title;
        this.color = color;
    }
}
