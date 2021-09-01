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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    }

    private final Class<T> clazz;
    private final Map<Integer, CellResolver> cellResolverMap = new HashMap<>();

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

            field.setAccessible(true);

            cellResolverMap.put(excelColumn.column(), cellResolver);
        }
    }

    protected List<T> read(Sheet sheet) throws Exception {

        int rows = sheet.getPhysicalNumberOfRows();

        List<T> list = new ArrayList<>(rows);
        for (int i = 1; i < rows; i++) {
            T readResult = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<Integer, CellResolver> entry : cellResolverMap.entrySet()) {
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(entry.getKey());

                Object val = Objects.isNull(cell) ? null : entry.getValue().converter.read(cell);
                Field field = entry.getValue().field;
                field.set(readResult, val);
            }
            list.add(readResult);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    protected void write(Sheet sheet, List<T> dataList) throws Exception {

        Row header = sheet.createRow(0);

        for (Map.Entry<Integer, CellResolver> entry : cellResolverMap.entrySet()) {

            CellResolver cellResolver = entry.getValue();

            Cell cell = header.createCell(cellResolver.excelColumn.column());
            cell.setCellType(CellType.STRING);
            cell.setCellValue(cellResolver.excelColumn.name());
        }

        int size = dataList.size();
        for (int i = 0; i < size; i++) {
            Row row = sheet.createRow(i + 1);
            T data = dataList.get(i);

            for (Map.Entry<Integer, CellResolver> entry : cellResolverMap.entrySet()) {

                CellResolver cellResolver = entry.getValue();
                Cell cell = row.createCell(cellResolver.excelColumn.column());

                cellResolver.converter.write(cell, cellResolver.field.get(data));
            }
        }
    }

}
