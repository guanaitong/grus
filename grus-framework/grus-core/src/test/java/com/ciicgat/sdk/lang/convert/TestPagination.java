/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import com.ciicgat.grus.json.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @Author: Jiaju.Wei
 * @Date: Created in 2018/7/3
 * @Description:
 */
@SuppressWarnings("unchecked")
public class TestPagination {

    @Test
    public void test() {
        Pagination<String> pagination = new Pagination();

        pagination.setDataList(Arrays.asList("123", "456"));
        pagination.setTotalCount(1000);
        pagination.setHasNext(false);

        Pagination<String> pagination1 = new Pagination(1000, Arrays.asList("123", "456"));

        Assertions.assertEquals(pagination, pagination1);
        Assertions.assertEquals(pagination.hashCode(), pagination1.hashCode());

        Assertions.assertEquals(pagination.toString(), pagination1.toString());

        Assertions.assertEquals(JSON.toJSONString(pagination), JSON.toJSONString(pagination1));
    }

    @Test
    public void testHasNext() {
        Pagination pagination = new Pagination(200, null, 10, 20);
        Assertions.assertEquals(Boolean.FALSE, pagination.isHasNext());
        pagination = new Pagination(200, null, 8, 20);
        Assertions.assertEquals(Boolean.TRUE, pagination.isHasNext());
        pagination = new Pagination(200, null, 11, 20);
        Assertions.assertEquals(Boolean.FALSE, pagination.isHasNext());
    }
}
