/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.util;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 生成工具类
 *
 * @author Clive Yuan
 * @date 2020/09/09
 */
public class GeneratorUtils {

    /**
     * 获取小写驼峰名
     *
     * <p> user -> user </p>
     * <p> User -> user </p>
     * <p> USER -> user </p>
     * <p> UsER -> usER </p>
     * <p> TemplateEnterprise -> templateEnterprise </p>
     * <p> t_user_info -> tUserInfo </p>
     * <p> user_list_detail -> userListDetail </p>
     * <p> USER_LIST_DETAIL -> userListDetail </p>
     *
     * @param str 字符串
     * @return
     */
    public static String getLowerCamelName(String str) {
        // 全大写, 包含下划线
        Objects.requireNonNull(str, "str can't be null");
        if (str.contains("_")) {
            return StringUtils.uncapitalize(lineToHump(str));
        }
        if (StringUtils.isAllUpperCase(str)) {
            return str.toLowerCase();
        }
        return StringUtils.uncapitalize(str);
    }

    private static String lineToHump(String str) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str.toUpperCase());
    }
}
