/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.cache;

/**
 * 二级缓存回调过度类
 *
 * @param <T>
 * @author Tom.hu
 */
public class IdObject<T> {

    private int id;
    private T object;

    public IdObject(int id, T object) {
        this.id = id;
        this.object = object;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

}
