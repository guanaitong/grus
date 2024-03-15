package com.ciicgat.grus.lock;

/**
 * Created by August.Zhou on 2024/3/15 21:27
 */
public class Result<T> {
    private final boolean success;

    private final T value;

    public Result(boolean success, T value) {
        this.success = success;
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getValue() {
        return value;
    }
}
