/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExcelUtilsTest {

    @ExcelSheet(type = ExcelType.XLSX, readIndex = 0, name = "第一页")
    public static class TestExcelBo {
        @ExcelColumn(name = "姓名", column = 0)
        private String name;
        @ExcelColumn(name = "年龄", column = 1)
        private int age;
        @ExcelColumn(name = "性别", column = 2)
        private String sex;
        @ExcelColumn(name = "生日", column = 3, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date birth;
        @ExcelColumn(name = "是否党员", column = 4)
        private Boolean isParty;
        @ExcelColumn(name = "体重", column = 5)
        private BigDecimal weight;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBirth() {
            return birth;
        }

        public void setBirth(Date birth) {
            this.birth = birth;
        }

        public Boolean getParty() {
            return isParty;
        }

        public void setParty(Boolean party) {
            isParty = party;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestExcelBo that = (TestExcelBo) o;
            return age == that.age &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(sex, that.sex) &&
                    Objects.equals(birth, that.birth) &&
                    Objects.equals(isParty, that.isParty) &&
                    Objects.equals(weight, that.weight);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, sex, birth, isParty, weight);
        }
    }

    @ExcelSheet(type = ExcelType.XLSX, name = "第二页")
    public static class TestExcelBo2 {
        @ExcelColumn(name = "金额", column = 0)
        private BigDecimal money;
        @ExcelColumn(name = "税率", column = 1)
        private int tax;

        public BigDecimal getMoney() {
            return money;
        }

        public void setMoney(BigDecimal money) {
            this.money = money;
        }

        public int getTax() {
            return tax;
        }

        public void setTax(int tax) {
            this.tax = tax;
        }
    }

    private List<TestExcelBo> dataList = new ArrayList<>();

    private List<TestExcelBo2> dataList2 = new ArrayList<>();


    @Before
    public void prepare() throws Exception {
        TestExcelBo testExcelBo = new TestExcelBo();
        testExcelBo.setAge(33);
        testExcelBo.setSex("男");
        testExcelBo.setName("张三");
        testExcelBo.setBirth(DateUtils.parseDate("1991-11-10 10:00:13", "yyyy-MM-dd HH:mm:ss"));
        testExcelBo.setParty(false);
        testExcelBo.setWeight(BigDecimal.valueOf(88.6));

        TestExcelBo testExcelBo1 = new TestExcelBo();
        testExcelBo1.setAge(25);
        testExcelBo1.setSex("女");
        testExcelBo1.setName("李四");
        testExcelBo1.setBirth(DateUtils.parseDate("2021-11-10 10:00:13", "yyyy-MM-dd HH:mm:ss"));
        testExcelBo1.setParty(true);
        testExcelBo1.setWeight(BigDecimal.valueOf(50.4));
        dataList.add(testExcelBo);
        dataList.add(testExcelBo1);

        TestExcelBo2 testExcelBo2 = new TestExcelBo2();
        testExcelBo2.setMoney(BigDecimal.valueOf(100));
        testExcelBo2.setTax(13);

        dataList2.add(testExcelBo2);
    }

    @Test
    public void readExcelTest() throws Exception {
        String filePath = this.getClass().getClassLoader().getResource("readTest.xls").getPath();
        FileInputStream inputStream = new FileInputStream(filePath);

        List<TestExcelBo> list = ExcelUtils.readExcel(inputStream, TestExcelBo.class);

        Assert.assertEquals(4, list.size());
        Assert.assertEquals("张三", list.get(0).getName());
        Assert.assertEquals(25, list.get(1).getAge());
        Assert.assertEquals(true, list.get(1).getParty());
        Assert.assertEquals(DateUtils.parseDate("1991-11-10 10:00:13", "yyyy-MM-dd HH:mm:ss"), list.get(2).getBirth());
        Assert.assertEquals(BigDecimal.valueOf(79.85), list.get(3).getWeight());
    }

    @Test
    public void writeExcelTest() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ExcelUtils.writeExcel(outputStream, dataList);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        List<TestExcelBo> list = ExcelUtils.readExcel(inputStream, TestExcelBo.class);

        Assert.assertEquals(2, list.size());
        Assert.assertEquals(dataList.get(0), list.get(0));
        Assert.assertEquals(dataList.get(1), list.get(1));
    }

    @Test
    public void testMulti() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExcelUtils.writeExcelMultiSheets(outputStream, Arrays.asList(dataList, dataList2));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        List<List<?>> list = ExcelUtils.readExcelMultiSheets(inputStream, Arrays.asList(TestExcelBo.class, TestExcelBo2.class));

        Assert.assertEquals(2, list.size());
        Assert.assertEquals(dataList.get(0), list.get(0).get(0));
    }

}
