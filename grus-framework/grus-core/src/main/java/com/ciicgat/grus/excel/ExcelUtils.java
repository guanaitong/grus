/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Jiaju Wei
 * @Date: 2021/03/02 10:33
 * @Description:
 */
public class ExcelUtils {
    private final static Map<Class<?>, ExcelResolver<?>> EXCEL_RESOLVER_MAP = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    @SuppressWarnings("unchecked")
    public static <T> List<T> readExcel(InputStream inputStream, Class<T> dstClass) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            ExcelSheet excelSheet = checkAndGetSheet(dstClass);

            return readOneList(workbook, excelSheet, dstClass);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<List<?>> readExcelMultiSheets(InputStream inputStream, List<Class<?>> dstClasses) throws Exception {
        List<List<?>> resultList = new ArrayList<>(dstClasses.size());
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            for (Class<?> clz : dstClasses) {
                ExcelSheet excelSheet = checkAndGetSheet(clz);
                List<?> result = readOneList(workbook, excelSheet, clz);
                resultList.add(result);
            }
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeExcel(OutputStream outputStream, List<T> dataList) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        Class<?> dstClass = dataList.get(0).getClass();
        ExcelSheet excelSheet = checkAndGetSheet(dstClass);
        Workbook wb = getWorkBook(excelSheet);

        writeOneList(wb, excelSheet, dstClass, dataList);
        wb.write(outputStream);
    }

    public static void writeExcelMultiSheets(OutputStream outputStream, List<List<?>> datasList) throws Exception {
        if (datasList == null || datasList.isEmpty()) {
            return;
        }

        // 取第一个列表的type来建文件
        List<?> datas = datasList.get(0);
        if (datas == null || datas.isEmpty()) {
            return;
        }

        Class<?> dstClass = datas.get(0).getClass();
        ExcelSheet excelSheet = checkAndGetSheet(dstClass);
        Workbook wb = getWorkBook(excelSheet);

        for (List<?> d : datasList) {
            if (d == null || d.isEmpty()) {
                continue;
            }
            Class<?> dclass = d.get(0).getClass();
            ExcelSheet dSheet = dclass.getAnnotation(ExcelSheet.class);
            writeOneList(wb, dSheet, dclass, d);
        }
        wb.write(outputStream);
    }

    private static ExcelSheet checkAndGetSheet(Class<?> clazz) {
        ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
        if (excelSheet == null) {
            throw new RuntimeException("ExcelSheet配置缺失");
        }
        return excelSheet;
    }

    private static Workbook getWorkBook(ExcelSheet excelSheet) {
        Workbook wb;
        if (excelSheet.type() == ExcelType.XLS) {
            wb = new HSSFWorkbook();
        } else {
            wb = new XSSFWorkbook();
        }
        return wb;
    }

    private static <T> List<T> readOneList(Workbook wb, ExcelSheet excelSheet, Class<T> dstClass) throws Exception {
        Sheet sheet;
        try {
            if (excelSheet.readIndex() != -1) {
                sheet = wb.getSheetAt(excelSheet.readIndex());
            } else {
                sheet = wb.getSheet(excelSheet.name());
            }
            if (sheet == null) {
                throw new RuntimeException("未找到指定sheet");
            }
        } catch (Exception e) {
            LOGGER.error("EXCEL_READ_ERR", e);
            throw e;
        }
        ExcelResolver<T> excelResolver;
        synchronized (ExcelUtils.class) {
            excelResolver = (ExcelResolver<T>) EXCEL_RESOLVER_MAP.get(dstClass);
            if (excelResolver == null) {
                excelResolver = new ExcelResolver<>(dstClass);
                EXCEL_RESOLVER_MAP.put(dstClass, excelResolver);
            }
        }

        return excelResolver.read(sheet);
    }

    private static void writeOneList(Workbook wb, ExcelSheet excelSheet, Class<?> dstClass, List<?> dataList) throws Exception {
        Sheet sheet;
        if (StringUtils.isEmpty(excelSheet.name())) {
            sheet = wb.createSheet();
        } else {
            sheet = wb.createSheet(excelSheet.name());
        }

        ExcelResolver excelResolver;
        synchronized (ExcelUtils.class) {
            excelResolver = EXCEL_RESOLVER_MAP.get(dstClass);
            if (excelResolver == null) {
                excelResolver = new ExcelResolver(dstClass);
                EXCEL_RESOLVER_MAP.put(dstClass, excelResolver);
            }
        }

        excelResolver.write(sheet, dataList);
    }

}
