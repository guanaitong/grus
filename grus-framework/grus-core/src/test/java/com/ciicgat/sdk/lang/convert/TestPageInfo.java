/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by August.Zhou on 2018-10-22 13:50.
 */
public class TestPageInfo {

    @Test
    public void test() {
        PageInfo<String> pageInfo = new PageInfo<>(2, 25);
        Object clone = pageInfo.clone();
        Assert.assertEquals(pageInfo, clone);
        Assert.assertEquals(pageInfo.getStart(), 25);
        Assert.assertEquals(pageInfo.getEnd(), 50);

        pageInfo.setOrder(PageInfo.ASC);
        pageInfo.setOrderBy("name");
        pageInfo.setList(Arrays.asList("123", "456"));
        System.out.println(pageInfo.toString());


        Assert.assertNotNull(pageInfo.toString());
        PageInfo<String> pageInfo1 = new PageInfo<>();

        Assert.assertEquals(pageInfo1.getStart(), 0);
        Assert.assertEquals(pageInfo1.getEnd(), 25);

    }
}
