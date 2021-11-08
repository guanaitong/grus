/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by August.Zhou on 2018-10-22 13:50.
 */
public class TestPageInfo {

    @Test
    public void test() {
        PageInfo<String> pageInfo = new PageInfo<>(2, 25);
        Assertions.assertEquals(pageInfo.getStart(), 25);
        Assertions.assertEquals(pageInfo.getEnd(), 50);

        pageInfo.setOrder(PageInfo.ASC);
        pageInfo.setOrderBy("name");
        pageInfo.setList(Arrays.asList("123", "456"));
        System.out.println(pageInfo.toString());


        Assertions.assertNotNull(pageInfo.toString());
        PageInfo<String> pageInfo1 = new PageInfo<>();

        Assertions.assertEquals(pageInfo1.getStart(), 0);
        Assertions.assertEquals(pageInfo1.getEnd(), 25);

    }
}
