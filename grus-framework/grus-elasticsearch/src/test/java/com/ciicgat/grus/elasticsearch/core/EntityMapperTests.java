/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import com.ciicgat.grus.elasticsearch.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Created by August.Zhou on 2019-09-11 13:12.
 */
public class EntityMapperTests {

    @Test
    public void test() {
        DefaultEntityMapper entityMapper = new DefaultEntityMapper(Message.class);
        Message message = new Message();
        message.setTimeCreated(new Date());
        message.setAppName("123");
        message.setAppInstance("xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        message.setDocId("xxx");
        message.setDocIndex("as;ldjfsadf");

        String s = entityMapper.mapToString(message);
        Assert.assertTrue(!s.contains("docId"));
        Assert.assertTrue(!s.contains("docIndex"));
        System.out.println(s);
    }
}
