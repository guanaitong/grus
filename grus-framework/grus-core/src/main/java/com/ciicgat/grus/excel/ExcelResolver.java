/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: Jiaju Wei
 * @Date: 2021/03/02 10:33
 * @Description:
 */
public class ExcelResolver<T> {

    private static class CellResolver {
        protected Field field;

        protected Converter converter;

        protected ExcelColumn excelColumn;

        protected int column;
    }

    private final Class<T> clazz;
    private final List<CellResolver> cellResolvers = new ArrayList<>();

    public ExcelResolver(Class<T> clazz) {
        this.clazz = clazz;
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (Objects.isNull(excelColumn)) {
                continue;
            }
            Converter<?> converter = ConverterFactory.newConverter(field.getType(), excelColumn);

            CellResolver cellResolver = new CellResolver();
            cellResolver.field = field;
            cellResolver.converter = converter;
            cellResolver.excelColumn = excelColumn;
            cellResolver.column = excelColumn.column();
            field.setAccessible(true);
            cellResolvers.add(cellResolver);
        }
    }

    protected List<T> read(Sheet sheet) throws Exception {
        int rows = sheet.getPhysicalNumberOfRows();
        List<T> list = new ArrayList<>(rows);
        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            T readResult = clazz.getDeclaredConstructor().newInstance();
            for (CellResolver cellResolver : cellResolvers) {
                Cell cell = row.getCell(cellResolver.column);
                if (Objects.nonNull(cell)) {
                    Object val = cellResolver.converter.read(cell);
                    Field field = cellResolver.field;
                    field.set(readResult, val);
                }
            }
            list.add(readResult);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    protected void write(Sheet sheet, List<T> dataList) throws Exception {
        Row header = sheet.createRow(0);
        for (CellResolver cellResolver : cellResolvers) {
            Cell cell = header.createCell(cellResolver.column);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(cellResolver.excelColumn.name());
        }

        int size = dataList.size();
        for (int i = 0; i < size; i++) {
            Row row = sheet.createRow(i + 1);
            T data = dataList.get(i);

            for (CellResolver cellResolver : cellResolvers) {
                Cell cell = row.createCell(cellResolver.column);

                cellResolver.converter.write(cell, cellResolver.field.get(data));
            }
        }
    }

}
