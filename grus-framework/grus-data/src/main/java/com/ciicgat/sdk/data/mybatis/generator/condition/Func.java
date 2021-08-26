/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.condition;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * 函数
 *
 * @author Clive Yuan
 * @date 2020/10/28
 */
public interface Func<Children, R> extends Serializable {


    /**
     * 字段 IS NULL
     * <p>例: isNull("name")</p>
     *
     * @param column 字段
     * @return children
     */
    Children isNull(R column);

    /**
     * 字段 IS NOT NULL
     * <p>例: isNotNull("name")</p>
     *
     * @param column 字段
     * @return children
     */
    Children isNotNull(R column);

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * <p>例: in("id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * <li> 如果集合为 empty 则不会进行 sql 拼接 </li>
     *
     * @param column 字段
     * @param coll   数据集合
     * @return children
     */
    Children in(R column, Collection<?> coll);

    /**
     * 字段 IN (v0, v1, ...)
     * <p>例: in("id", 1, 2, 3, 4, 5)</p>
     *
     * <li> 如果动态数组为 empty 则不会进行 sql 拼接 </li>
     *
     * @param column 字段
     * @param values 数据数组
     * @return children
     */
    default Children in(R column, Object... values) {
        return in(column, Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
                .collect(toList()));
    }

    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
     * <p>例: notIn("id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * @param column 字段
     * @param coll   数据集合
     * @return children
     */
    Children notIn(R column, Collection<?> coll);

    /**
     * 字段 NOT IN (v0, v1, ...)
     * <p>例: notIn("id", 1, 2, 3, 4, 5)</p>
     *
     * @param column 字段
     * @param values 数据数组
     * @return children
     */
    default Children notIn(R column, Object... values) {
        return notIn(column, Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
                .collect(toList()));
    }
}
