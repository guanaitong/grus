/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

/**
 * Created by August.Zhou on 2017/7/3 18:05.
 */
public interface BeanLoader<T> {

    T load(String content);


}
