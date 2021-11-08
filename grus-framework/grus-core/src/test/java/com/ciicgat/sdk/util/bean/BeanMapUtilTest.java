/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/15 15:59
 * @Description:
 */
public class BeanMapUtilTest {

    public static class Bean {
        private static String ss;
        private String app;
        private Integer num;
        private Date date;
        private boolean isOk;
        private boolean autoRuntimeDialect;
        private String privateField;

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public boolean isOk() {
            return isOk;
        }

        public void setOk(boolean ok) {
            isOk = ok;
        }

        public boolean isAutoRuntimeDialect() {
            return autoRuntimeDialect;
        }

        public void setAutoRuntimeDialect(boolean autoRuntimeDialect) {
            this.autoRuntimeDialect = autoRuntimeDialect;
        }

        private String getPrivateField() {
            return privateField;
        }

        public void setPrivateField(String privateField) {
            this.privateField = privateField;
        }

        public static String getSs() {
            return ss;
        }

        public static void setSs(String ss) {
            Bean.ss = ss;
        }
    }

    @Test
    public void testBean2Map() throws ParseException {
        Bean bean = new Bean();
        bean.setApp("111");
        bean.setNum(4);
        bean.setDate(DateUtils.parseDate("2019-09-09 00:00:00", "yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> beanMap = BeanMapUtil.bean2Map(bean);
        Assert.assertEquals(5, beanMap.size());
        Assert.assertEquals("111", beanMap.get("app"));
        Assert.assertEquals(4, beanMap.get("num"));
        Assert.assertEquals(DateUtils.parseDate("2019-09-09 00:00:00", "yyyy-MM-dd HH:mm:ss"), beanMap.get("date"));

        System.out.println("finished");
    }

    @Test
    public void testMap2Bean() throws Exception {
        Map<String, Object> beanMap = new HashMap<>();
        beanMap.put("app", "111");
        beanMap.put("num", 4);
        beanMap.put("date", DateUtils.parseDate("2019-09-09 00:00:00", "yyyy-MM-dd HH:mm:ss"));

        Bean bean = BeanMapUtil.map2Bean(beanMap, Bean.class);

        Assert.assertEquals("111", bean.getApp());
        Assert.assertEquals(Integer.valueOf(4), bean.getNum());
        Assert.assertEquals(DateUtils.parseDate("2019-09-09 00:00:00", "yyyy-MM-dd HH:mm:ss"), bean.getDate());
        System.out.println("finished");
    }
}
