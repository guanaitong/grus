/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.db;

import java.util.Arrays;
import java.util.List;

/**
 * 类型解析器
 *
 * @author Clive Yuan
 * @date 2020/11/04
 */
public class JdbcTypeResolver {

    private static final String UNSIGNED = " UNSIGNED";
    private static final String BIT = "BIT";

    /**
     * 解析类型
     *
     * @param type   jdbc类型
     * @param length 长度
     * @return
     */
    public static JdbcType resolve(String type, Integer length) {
        if (type.contains(UNSIGNED)) {
            type = type.replace(UNSIGNED, "");
        }
        String finalType = type;
        JdbcType jdbcType = Arrays.stream(JdbcType.values())
                .filter(x -> List.of(x.getSqlTypes()).contains(finalType))
                .findAny()
                .orElse(JdbcType.OBJECT);
        if (BIT.equals(finalType) && length == 1) {
            jdbcType = JdbcType.BOOLEAN;
        }
        return jdbcType;
    }
}
