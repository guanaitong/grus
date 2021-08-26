/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache;

/**
 * Created by August.Zhou on 2019-08-21 15:10.
 */
public interface RedisKeyListener {
    RedisKeyListener DEFAULT = new RedisKeyListener() {
        @Override
        public void onPut(byte[] key) {

        }

        @Override
        public void onDelete(byte[] key) {

        }
    };

    void onPut(byte[] key);

    void onDelete(byte[] key);
}
