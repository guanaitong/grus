/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

/**
 * Created by August.Zhou on 2018-11-06 16:05.
 */
public interface ConfigChangeListener {


    /**
     * 该方法会在gconf后台同步数据线程里执行，请保证该方法不要有阻塞。不然会影响gconf更新。
     *
     * @param key      键
     * @param oldValue 老的值,新增key时，该值为null
     * @param newValue 新的值,删除key时，该值为null
     */
    void valueChanged(String key, String oldValue, String newValue);
}
