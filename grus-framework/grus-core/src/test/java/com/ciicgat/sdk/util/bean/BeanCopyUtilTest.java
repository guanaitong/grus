/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import com.ciicgat.sdk.lang.convert.Pagination;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/14 18:24
 * @Description:
 */
public class BeanCopyUtilTest {

    public static class Bean1 {
        private String app;
        private Integer num;
        private Attr attr;

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

        public Attr getAttr() {
            return attr;
        }

        public void setAttr(Attr attr) {
            this.attr = attr;
        }
    }

    public static class Bean2 {
        private String app;
        private Integer num;
        private Attr attr;

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

        public Attr getAttr() {
            return attr;
        }

        public void setAttr(Attr attr) {
            this.attr = attr;
        }
    }

    public static class Attr {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class TestExtraConverter implements BeanExtraConverter<Bean1, Bean2> {
        @Override
        public void afterProcess(Bean1 src, Bean2 dst) {
            dst.setApp("改过了");
        }
    }

    private TestExtraConverter testExtraConverter = new TestExtraConverter();

    @Test
    public void testCopy() {
        Bean1 bean1 = new Bean1();
        bean1.setApp("支付");
        bean1.setNum(1);
        Attr attr = new Attr();
        attr.setName("wjj");
        bean1.setAttr(attr);

        Bean2 bean2 = BeanCopyUtil.copy(bean1, Bean2.class);
        Bean2 bean3 = BeanCopyUtil.copy(bean1, Bean2.class, testExtraConverter);

        Assert.assertEquals("支付", bean2.getApp());
        Assert.assertEquals(Integer.valueOf(1), bean2.getNum());
        Assert.assertEquals("wjj", bean2.getAttr().getName());

        Assert.assertEquals("改过了", bean3.getApp());
        Assert.assertEquals(Integer.valueOf(1), bean3.getNum());
        Assert.assertEquals("wjj", bean3.getAttr().getName());
    }

    @Test
    public void testCopyList() {
        List<Bean1> bean1s = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Bean1 bean1 = new Bean1();
            bean1.setApp("我们" + i);
            bean1.setNum(i);
            Attr attr = new Attr();
            attr.setName("wjj");
            bean1.setAttr(attr);
            bean1s.add(bean1);
        }

        List<Bean2> bean2s = BeanCopyUtil.copyList(bean1s, Bean2.class, testExtraConverter);
        List<Bean2> bean3s = BeanCopyUtil.copyList(bean1s, Bean2.class);

        Assert.assertEquals(5, bean2s.size());
        Assert.assertEquals("改过了", bean2s.get(2).getApp());
        Assert.assertEquals(Integer.valueOf(2), bean2s.get(2).getNum());
        Assert.assertEquals("wjj", bean2s.get(2).getAttr().getName());

        Assert.assertEquals(5, bean3s.size());
        Assert.assertEquals("我们2", bean3s.get(2).getApp());
        Assert.assertEquals(Integer.valueOf(2), bean3s.get(2).getNum());
        Assert.assertEquals("wjj", bean3s.get(2).getAttr().getName());
    }

    @Test
    public void testCopyPagination() {
        List<Bean1> bean1s = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Bean1 bean1 = new Bean1();
            bean1.setApp("我们" + i);
            bean1.setNum(i);
            Attr attr = new Attr();
            attr.setName("wjj");
            bean1.setAttr(attr);
            bean1s.add(bean1);
        }

        Pagination<Bean1> bean1P = new Pagination<>(10, bean1s, 1, 5);

        Pagination<Bean2> bean2P = BeanCopyUtil.copyPagination(bean1P, Bean2.class);
        Pagination<Bean2> bean3P = BeanCopyUtil.copyPagination(bean1P, Bean2.class, (src, dst) -> {
            dst.setApp("lamda");
        });

        Assert.assertTrue(bean2P.isHasNext());
        Assert.assertEquals("我们2", bean2P.getDataList().get(2).getApp());
        Assert.assertEquals(Integer.valueOf(2), bean2P.getDataList().get(2).getNum());
        Assert.assertEquals(10, bean2P.getTotalCount());

        Assert.assertTrue(bean3P.isHasNext());
        Assert.assertEquals("lamda", bean3P.getDataList().get(2).getApp());
        Assert.assertEquals(Integer.valueOf(2), bean3P.getDataList().get(2).getNum());
        Assert.assertEquals(10, bean3P.getTotalCount());
    }
}
