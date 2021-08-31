/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.security;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 用于数据脱敏处理
 * Created by August.Zhou on 2020-04-07 17:33.
 */
public class Masker {

    /**
     * 姓名脱敏
     *
     * @param name
     * @return
     */
    public static String maskName(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        int len = name.length();
        if (len == 1) {
            return "*";
        } else {
            StringBuilder sb = new StringBuilder();
            // 奇数位设置为*
            for (int i = 0; i < len; i++) {
                if ((i & 1) == 0) {
                    sb.append(name.charAt(i));
                } else {
                    sb.append('*');
                }
            }
            return sb.toString();
        }
    }

    private static final Pattern PATTERN_PHONE = Pattern.compile("(\\w{3})\\w{4}(\\w{4})");

    /**
     * 手机号脱敏
     *
     * @param phoneNum
     * @return
     */
    public static String maskPhoneNum(String phoneNum) {
        if (StringUtils.isEmpty(phoneNum)) {
            return phoneNum;
        }
        //国内手机号
        if (phoneNum.length() == 11) {
            return PATTERN_PHONE.matcher(phoneNum).replaceAll("$1****$2");
        }
        return phoneNum;
    }


    /**
     * 邮箱脱敏
     *
     * @param email
     * @return
     */
    public static String maskEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return email;
        }
        int index = email.indexOf('@');
        if (index == -1) {
            return email;
        }
        String emailHead = email.substring(0, index);
        String emailLast = email.substring(index);
        StringBuilder sb = new StringBuilder();
        int length = emailHead.length();
        if (length > 6) {
            sb.append(emailHead, 0, 2);
            for (int i = 2; i < length - 2; i++) {
                sb.append('*');
            }
            sb.append(emailHead, length - 2, length);
        } else if (length > 4) {
            sb.append(emailHead, 0, 2);
            for (int i = 2; i < length - 1; i++) {
                sb.append('*');
            }
            sb.append(emailHead, length - 1, length);
        } else {
            sb.append(emailHead, 0, 1);
            for (int i = 1; i < length; i++) {
                sb.append('*');
            }
        }
        sb.append(emailLast);
        return sb.toString();
    }


    private static final Pattern PATTERN_ID_NUM = Pattern.compile("(\\w{4})\\w{10}(\\w{4})");

    /**
     * 身份证号码脱敏
     *
     * @param idNum
     * @return
     */
    public static String maskIdNum(String idNum) {
        if (StringUtils.isEmpty(idNum)) {
            return idNum;
        }
        if (idNum.length() == 18) {
            return PATTERN_ID_NUM.matcher(idNum).replaceAll("$1**********$2");
        }
        return idNum;
    }
}
