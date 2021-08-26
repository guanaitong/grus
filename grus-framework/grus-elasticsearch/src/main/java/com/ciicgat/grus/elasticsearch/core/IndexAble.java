/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import java.util.Date;
import java.util.function.Supplier;

/**
 * Created by August.Zhou on 2019-09-11 13:18.
 */
public interface IndexAble {
    String getDocId();

    void setDocId(String docId);

    /**
     * 获取查询的对象对应的index。一般save的时候不用管。
     *
     * @return
     */
    String getDocIndex();

    void setDocIndex(String docIndex);

    default Supplier<Date> getTimestampSupplier() {
        return () -> new Date();
    }

    default Supplier<String> getPipelineSupplier() {
        return () -> "";
    }

}
