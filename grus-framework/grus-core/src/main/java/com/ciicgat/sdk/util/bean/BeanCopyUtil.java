/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import com.ciicgat.sdk.lang.convert.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/14 18:10
 * @Description: bean拷贝工具
 */
public class BeanCopyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanCopyUtil.class);

    private static ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();

    public static <T> T copy(Object src, Class<T> dstClass) {
        if (src == null) {
            return null;
        }

        BeanCopier beanCopier = getCopier(src.getClass(), dstClass);
        T dst;
        try {
            dst = dstClass.getDeclaredConstructor().newInstance();
            beanCopier.copy(src, dst, null);
        } catch (Exception e) {
            LOGGER.error("BeanCopyUtil cglib copy error", e);
            throw new RuntimeException(e);
        }
        return dst;
    }


    public static <T, R> R copy(T src, Class<R> dstClass, BeanExtraConverter<T, R> converter) {
        R dst = copy(src, dstClass);
        converter.afterProcess(src, dst);

        return dst;
    }

    public static <T, R> List<R> copyList(List<T> srcList, Class<R> dstClass) {
        return doCopyList(srcList, dstClass, null);
    }

    public static <T, R> List<R> copyList(List<T> srcList, Class<R> dstClass, BeanExtraConverter<T, R> converter) {
        return doCopyList(srcList, dstClass, converter);
    }

    public static <T, R> Pagination<R> copyPagination(Pagination<T> src, Class<R> dstClass) {
        List<R> dataList = doCopyList(src.getDataList(), dstClass, null);
        Pagination<R> dst = new Pagination<>(src.getTotalCount(), dataList);
        dst.setHasNext(src.isHasNext());

        return dst;
    }

    public static <T, R> Pagination<R> copyPagination(Pagination<T> src, Class<R> dstClass, BeanExtraConverter<T, R> converter) {
        List<R> dataList = doCopyList(src.getDataList(), dstClass, converter);
        Pagination<R> dst = new Pagination<>(src.getTotalCount(), dataList);
        dst.setHasNext(src.isHasNext());

        return dst;
    }

    private static BeanCopier getCopier(Class srcClass, Class dstClass) {
        String key = srcClass.getName() + dstClass.getName();
        BeanCopier beanCopier = BEAN_COPIER_CACHE.get(key);
        if (null == beanCopier) {
            BeanCopier newBeanCopier = BeanCopier.create(srcClass, dstClass, false);
            beanCopier = BEAN_COPIER_CACHE.putIfAbsent(key, newBeanCopier);
            // 线程安全，保证取到的是一个copier
            if (beanCopier == null) {
                beanCopier = newBeanCopier;
            }
        }

        return beanCopier;
    }

    @SuppressWarnings("unchecked")
    private static <T, R> List<R> doCopyList(List<T> srcList, Class<R> dstClass, BeanExtraConverter<T, R> converter) {
        if (srcList == null) {
            return null;
        }
        if (srcList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        BeanCopier beanCopier = getCopier(srcList.get(0).getClass(), dstClass);

        List<R> dstList = new ArrayList<>();
        try {
            for (T src : srcList) {
                R dst = dstClass.getDeclaredConstructor().newInstance();
                beanCopier.copy(src, dst, null);
                if (converter != null) {
                    converter.afterProcess(src, dst);
                }
                dstList.add(dst);
            }
        } catch (Exception e) {
            LOGGER.error("BeanCopyUtil cglib copy error", e);
            throw new RuntimeException(e);
        }
        return dstList;
    }
}
