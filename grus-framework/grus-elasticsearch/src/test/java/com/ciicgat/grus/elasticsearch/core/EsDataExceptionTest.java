/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import org.elasticsearch.ElasticsearchException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created by August.Zhou on 2023/12/1 15:13
 */
class EsDataExceptionTest {

    @Test
    void convert() {
        new EsDataException();
        new EsDataException("1");
        new EsDataException("1", new Exception());
        new EsDataException(new Exception());
        Assertions.assertEquals(ElasticsearchException.class, EsDataException.convert(new ElasticsearchException("")).getCause().getClass());
        Assertions.assertEquals(ElasticsearchException.class, EsDataException.convert(new IOException(new ElasticsearchException(""))).getCause().getClass());
    }
}
