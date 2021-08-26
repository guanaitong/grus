/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import com.ciicgat.grus.excel.converter.BigDecimalConverter;
import com.ciicgat.grus.excel.converter.BooleanConverter;
import com.ciicgat.grus.excel.converter.ByteConverter;
import com.ciicgat.grus.excel.converter.CharConverter;
import com.ciicgat.grus.excel.converter.DateConverter;
import com.ciicgat.grus.excel.converter.DefaultFormatConverter;
import com.ciicgat.grus.excel.converter.DoubleConverter;
import com.ciicgat.grus.excel.converter.FloatConverter;
import com.ciicgat.grus.excel.converter.IntegerConverter;
import com.ciicgat.grus.excel.converter.LongConverter;
import com.ciicgat.grus.excel.converter.ShortConverter;
import com.ciicgat.grus.excel.converter.StringConverter;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 工厂类 生产出对应有效的 converter
 *
 * @Auther: chunhong.Wan
 * @Date: 2021/1/7 17:51
 * @Description:
 */
public class ConverterFactory {
    private final static Map<Class<?>, Class<? extends Converter<?>>> converterClassMap = new IdentityHashMap<>();


    static {
        //init load converters
        initDefaultConverter();
    }

    private static void initDefaultConverter() {
        converterClassMap.put(int.class, IntegerConverter.class);
        converterClassMap.put(Integer.class, IntegerConverter.class);

        converterClassMap.put(long.class, LongConverter.class);
        converterClassMap.put(Long.class, LongConverter.class);

        converterClassMap.put(byte.class, ByteConverter.class);
        converterClassMap.put(Byte.class, ByteConverter.class);

        converterClassMap.put(double.class, DoubleConverter.class);
        converterClassMap.put(Double.class, DoubleConverter.class);

        converterClassMap.put(float.class, FloatConverter.class);
        converterClassMap.put(Float.class, FloatConverter.class);

        converterClassMap.put(short.class, ShortConverter.class);
        converterClassMap.put(Short.class, ShortConverter.class);

        converterClassMap.put(char.class, CharConverter.class);
        converterClassMap.put(Character.class, CharConverter.class);

        converterClassMap.put(boolean.class, BooleanConverter.class);
        converterClassMap.put(Boolean.class, BooleanConverter.class);

        converterClassMap.put(String.class, StringConverter.class);
        converterClassMap.put(BigDecimal.class, BigDecimalConverter.class);
        converterClassMap.put(Date.class, DateConverter.class);
    }

    public static Converter<?> getConverter(Class<?> fieldType, ExcelColumn excelColumn) {
        Class<? extends Converter<?>> converterClass = converterClassMap.get(fieldType);
        if (converterClass == null) {
            return new DefaultFormatConverter();
        }

        try {
            Constructor<?> constructor = converterClass.getDeclaredConstructor(ExcelColumn.class);
            return (Converter<?>) constructor.newInstance(excelColumn);

        } catch (Exception e) {
            try {
                Constructor<?> constructor = converterClass.getDeclaredConstructor();
                return (Converter<?>) constructor.newInstance();
            } catch (Exception ex) {
                return new DefaultFormatConverter();
            }
        }
    }
}
