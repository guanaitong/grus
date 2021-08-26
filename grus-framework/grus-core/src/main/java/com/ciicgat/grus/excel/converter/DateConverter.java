/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel.converter;

import com.ciicgat.grus.excel.Converter;
import com.ciicgat.grus.excel.ExcelColumn;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.text.ParseException;
import java.util.Date;

/**
 * @Auther: chunhong.Wan
 * @Date: 2021/1/6 16:07
 * @Description:
 */
public class DateConverter implements Converter<Date> {

    private ExcelColumn excelColumn;

    public DateConverter(ExcelColumn excelColumn) {
        this.excelColumn = excelColumn;
    }

    @Override
    public Date read(Cell cell) {

        switch (cell.getCellTypeEnum()) {
            case STRING:
                String val = cell.getStringCellValue();
                try {
                    return DateUtils.parseDate(val, excelColumn.pattern());
                } catch (ParseException e) {
                    return null;
                }
            case NUMERIC:
            case BOOLEAN:
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    @Override
    public void write(Cell cell, Date date) {
        cell.setCellType(CellType.STRING);
        cell.setCellValue(DateFormatUtils.format(date, excelColumn.pattern()));
    }
}
