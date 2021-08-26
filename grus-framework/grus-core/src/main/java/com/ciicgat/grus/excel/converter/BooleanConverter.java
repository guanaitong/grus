/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel.converter;

import com.ciicgat.grus.excel.Converter;
import com.ciicgat.sdk.lang.constant.GatBoolean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class BooleanConverter implements Converter<Boolean> {
    @Override
    public Boolean read(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String val = cell.getStringCellValue();
                if ("true".equals(val)) {
                    return true;
                } else if ("false".equals(val)) {
                    return false;
                } else {
                    return null;
                }
            case NUMERIC:
                Number number = cell.getNumericCellValue();
                Integer result = number.intValue();
                if (GatBoolean.isTrue(result)) {
                    return true;
                } else if (GatBoolean.isFalse(result)) {
                    return false;
                } else {
                    return null;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    @Override
    public void write(Cell cell, Boolean aBoolean) {
        cell.setCellType(CellType.BOOLEAN);
        cell.setCellValue(aBoolean);
    }
}
