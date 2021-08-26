/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel.converter;

import com.ciicgat.grus.excel.Converter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * java-type Byte Converter
 *
 * @Auther: chunhong.Wan
 * @Date: 2021/1/6 16:07
 * @Description:
 */
public class ByteConverter implements Converter<Byte> {
    @Override
    public Byte read(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String num = cell.getStringCellValue();
                return Byte.valueOf(num);
            case NUMERIC:
                Number number = cell.getNumericCellValue();
                return number.byteValue();
            case BOOLEAN:
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    @Override
    public void write(Cell cell, Byte aByte) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(aByte.doubleValue());
    }
}
