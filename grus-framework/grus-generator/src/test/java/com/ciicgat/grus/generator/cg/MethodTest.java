/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg;

import com.ciicgat.grus.generator.cg.core.GeneratorChain;
import com.ciicgat.grus.generator.cg.util.GeneratorUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Clive Yuan
 * @date 2020/11/04
 */
public class MethodTest {
    @Test
    public void test() {

        System.out.println(byte[].class.getSimpleName());
    }

    @Test
    public void getLowerCamelName() {
        String str = "user -> user\n" +
                "User -> user\n" +
                "USER -> user\n" +
                "UsER -> usER\n" +
                "TemplateEnterprise -> templateEnterprise\n" +
                "t_user_info -> tUserInfo\n" +
                "user_list_detail -> userListDetail\n" +
                "USER_LIST_DETAIL -> userListDetail";
        String[] array = str.split("\n");
        for (String s : array) {
            String[] split = s.split(" -> ");
            String target = split[0];
            String expected = split[1];
            String result = GeneratorUtils.getLowerCamelName(target);
            System.out.printf("%s => %s%n", target, result);
            Assert.assertEquals(expected, result);
        }
    }
}
