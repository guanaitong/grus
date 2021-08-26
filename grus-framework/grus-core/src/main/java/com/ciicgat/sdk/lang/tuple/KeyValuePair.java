/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tuple;

import java.io.Serializable;

/**
 * Created by August.Zhou on 2017/7/27 18:04.
 */
public class KeyValuePair<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final K key;

    private final V value;


    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }


    public V getValue() {
        return value;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("key=").append(key);
        sb.append(",value=").append(value);
        return sb.toString();
    }
}
