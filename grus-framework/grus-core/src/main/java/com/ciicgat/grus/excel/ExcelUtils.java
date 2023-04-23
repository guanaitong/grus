/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Jiaju Wei
 * @Date: 2021/03/02 10:33
 * @Description:
 */
public class ExcelUtils {
    private final static Map<Class<?>, ExcelResolver<?>> EXCEL_RESOLVER_MAP = new IdentityHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);
    private static final Integer USE_FILE_CACHE_SIZE = 10000;

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
        writeExcel(outputStream, dataList, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeExcel(OutputStream outputStream, List<T> dataList, List<Integer> ignoreColumns) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        Class<?> dstClass = dataList.get(0).getClass();
        ExcelSheet excelSheet = checkAndGetSheet(dstClass);

        try (Workbook wb = getWorkBook(excelSheet, dataList.size())) {
            writeOneList(wb, excelSheet, dstClass, dataList, ignoreColumns);
            wb.write(outputStream);
        }
    }

    @SuppressWarnings("unchecked")
    public static void writeExcelMultiSheets(OutputStream outputStream, List<List<?>> datasList) throws Exception {
        writeExcelMultiSheets(outputStream, datasList, null);
    }

    @SuppressWarnings("unchecked")
    public static void writeExcelMultiSheets(OutputStream outputStream, List<List<?>> datasList, List<List<Integer>> ignoreColumns) throws Exception {
        if (datasList == null || datasList.isEmpty()) {
            return;
        }

        // 取第一个列表的type来建文件
        List<?> datas = datasList.get(0);
        if (datas == null || datas.isEmpty()) {
            return;
        }

        int size = datasList.stream().map(List::size).reduce(Integer::sum).orElse(0);

        Class<?> dstClass = datas.get(0).getClass();
        ExcelSheet excelSheet = checkAndGetSheet(dstClass);

        try (Workbook wb = getWorkBook(excelSheet, size)) {
            int i = 0;
            // 支持人为指定剔除已经申明的列。由调用方根据实际数据确定列是否要展示
            for (List<?> d : datasList) {
                List<Integer> ignoreList = ignoreColumns == null ? new ArrayList<>() : ignoreColumns.get(i);
                i++;
                if (d == null || d.isEmpty()) {
                    continue;
                }
                Class<?> dclass = d.get(0).getClass();
                ExcelSheet dSheet = dclass.getAnnotation(ExcelSheet.class);
                writeOneList(wb, dSheet, dclass, d, ignoreList);
            }
            wb.write(outputStream);
        }
    }

    private static ExcelSheet checkAndGetSheet(Class<?> clazz) {
        ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
        if (excelSheet == null) {
            throw new RuntimeException("ExcelSheet配置缺失");
        }
        return excelSheet;
    }

    private static Workbook getWorkBook(ExcelSheet excelSheet, Integer dataCount) {
        Workbook wb;
        if (dataCount >= USE_FILE_CACHE_SIZE) {
            wb = new SXSSFWorkbook(USE_FILE_CACHE_SIZE);
            return wb;
        }

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
        ExcelResolver excelResolver = getExcelResolver(dstClass);
        return excelResolver.read(sheet);
    }

    private static void writeOneList(Workbook wb, ExcelSheet excelSheet, Class<?> dstClass, List<?> dataList, List<Integer> ignoreColumns) throws Exception {
        Sheet sheet;
        if (StringUtils.isEmpty(excelSheet.name())) {
            sheet = wb.createSheet();
        } else {
            sheet = wb.createSheet(excelSheet.name());
        }

        ExcelResolver excelResolver = getExcelResolver(dstClass);

        excelResolver.write(sheet, dataList, ignoreColumns);
    }

    private static ExcelResolver getExcelResolver(Class<?> dstClass) {
        ExcelResolver excelResolver = EXCEL_RESOLVER_MAP.get(dstClass);
        if (excelResolver == null) {
            synchronized (ExcelUtils.class) {
                excelResolver = EXCEL_RESOLVER_MAP.get(dstClass);
                if (excelResolver == null) {
                    excelResolver = new ExcelResolver(dstClass);
                    EXCEL_RESOLVER_MAP.put(dstClass, excelResolver);
                }
            }
        }
        return excelResolver;
    }

    public static void removeCache(Class<?> clazz) {
        EXCEL_RESOLVER_MAP.remove(clazz);
    }
}
