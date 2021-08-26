/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @Auther: chunhong.Wan
 * @Date: 2021/1/6 16:02
 * @Description:
 */
public interface Converter<T> {

    T read(Cell cell);

    void write(Cell cell, T t);
}
