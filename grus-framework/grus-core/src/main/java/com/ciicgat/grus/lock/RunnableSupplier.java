/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.lock;

import java.util.function.Supplier;

/**
 * @author August
 * @date 3/15/2024 2:17 PM
 */
class RunnableSupplier<T> implements Runnable {

    private final Supplier<T> supplier;

    private T value;

    RunnableSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void run() {
        value = supplier.get();
    }

    public T value() {
        return value;
    }
}
