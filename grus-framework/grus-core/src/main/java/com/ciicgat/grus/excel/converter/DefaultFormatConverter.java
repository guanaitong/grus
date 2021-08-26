/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel.converter;

import com.ciicgat.grus.excel.Converter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * @Auther: chunhong.Wan
 * @Date: 2021/1/6 16:07
 * @Description:
 */
public class DefaultFormatConverter implements Converter<Object> {
    @Override
    public Object read(Cell cell) {
        return null;
    }

    @Override
    public void write(Cell cell, Object o) {
    }
}
