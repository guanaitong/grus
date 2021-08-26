/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel.converter;

import com.ciicgat.grus.excel.Converter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @Auther: chunhong.Wan
 * @Date: 2021/1/6 16:07
 * @Description:
 */
public class DoubleConverter implements Converter<Double> {
    @Override
    public Double read(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String num = cell.getStringCellValue();
                return Double.valueOf(num);
            case NUMERIC:
                Number number = cell.getNumericCellValue();
                return number.doubleValue();
            case BOOLEAN:
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    @Override
    public void write(Cell cell, Double aDouble) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(aDouble);
    }
}
