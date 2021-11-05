/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import com.ciicgat.sdk.lang.convert.Pagination;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/14 18:10
 * @Description: bean拷贝工具
 */
public class BeanCopyUtil {

    public static <T> T copy(Object src, Class<T> dstClass) {
        if (src == null) {
            return null;
        }

        T dst = BeanUtils.instantiateClass(dstClass);
        BeanUtils.copyProperties(src, dst);
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


    @SuppressWarnings("unchecked")
    private static <T, R> List<R> doCopyList(List<T> srcList, Class<R> dstClass, BeanExtraConverter<T, R> converter) {
        if (srcList == null) {
            return null;
        }
        if (srcList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<R> dstList = new ArrayList<>();
        for (T src : srcList) {
            R dst = BeanUtils.instantiateClass(dstClass);
            BeanUtils.copyProperties(src, dst);
            if (converter != null) {
                converter.afterProcess(src, dst);
            }
            dstList.add(dst);
        }
        return dstList;
    }
}
