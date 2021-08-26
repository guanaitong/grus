/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel.converter;

import com.ciicgat.grus.excel.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @Auther: chunhong.Wan
 * @Date: 2021/1/6 16:07
 * @Description:
 */
public class CharConverter implements Converter<Character> {
    @Override
    public Character read(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String val = cell.getStringCellValue();
                return val.charAt(0);
            case NUMERIC:
            case BOOLEAN:
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    @Override
    public void write(Cell cell, Character character) {
        cell.setCellType(CellType.STRING);
        cell.setCellValue(character == null ? StringUtils.EMPTY : String.valueOf(character));
    }
}
