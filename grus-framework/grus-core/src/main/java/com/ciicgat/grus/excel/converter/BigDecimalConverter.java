/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel.converter;

import com.ciicgat.grus.excel.Converter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;

/**
 * java-type BigDecimal
 *
 * @Auther: chunhong.Wan
 * @Date: 2021/1/6 16:07
 * @Description:
 */
public class BigDecimalConverter implements Converter<BigDecimal> {
    @Override
    public BigDecimal read(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String num = cell.getStringCellValue();
                return new BigDecimal(num);
            case NUMERIC:
                Number number = cell.getNumericCellValue();
                return BigDecimal.valueOf(number.doubleValue());
            case BOOLEAN:
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    @Override
    public void write(Cell cell, BigDecimal bigDecimal) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(bigDecimal.doubleValue());
    }
}
