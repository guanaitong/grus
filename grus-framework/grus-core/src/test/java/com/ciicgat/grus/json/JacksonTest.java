/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解决Jackson转换精度丢失问题
 * Created by jiaju.wei on 2018/8/23 17:43.
 */
public class JacksonTest {

    static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testMoney() throws Exception {
        TestModel testModel = new TestModel();
        testModel.setName("point");
        testModel.setBalance(new BigDecimal(5).setScale(2, BigDecimal.ROUND_FLOOR));

        String json = JSON.toJSONString(testModel);


//                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS,true);

        JavaType javaType = objectMapper.constructType(TestModel.class);
        JsonNode rootNode = objectMapper.readTree(json);
        JsonParser jsonParser = objectMapper.treeAsTokens(rootNode);
        TestModel dest = objectMapper.readValue(jsonParser, javaType);
        //修复精度问题
        Assert.assertEquals("5.00", dest.getBalance().toPlainString());
    }

    @Test
    public void testDate() throws Exception {
        String t = "2018-06-13 10:48:52";

        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("name", "point");
        jsonObject.put("time", t);
        //jsonObject.put("time2", "2018-06-13 10:48:52.000");
        jsonObject.put("time3", "2018-06-13");
        jsonObject.put("time4", "2015-12-06T11:18:57.000+0800");
        Date date5 = new Date();
        jsonObject.put("time5", date5.toString());
        jsonObject.put("time6", date5.getTime());

        String json = JSON.toJSONString(jsonObject);

        TestModel result = JSON.parse(json, TestModel.class);
        Assert.assertEquals(t, DateFormatUtils.format(result.getTime(), "yyyy-MM-dd HH:mm:ss"));
        //Assert.assertEquals("2018-06-13 10:48:52.000", DateFormatUtils.format(result.getTime2(), "yyyy-MM-dd HH:mm:ss.SSS"));
        Assert.assertEquals("2018-06-13", DateFormatUtils.format(result.getTime3(), "yyyy-MM-dd"));
        Assert.assertEquals("2015-12-06 11:18:57", DateFormatUtils.format(result.getTime4(), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals(DateFormatUtils.format(date5, "yyyy-MM-dd HH:mm:ss"), DateFormatUtils.format(result.getTime5(), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals(DateFormatUtils.format(date5, "yyyy-MM-dd HH:mm:ss"), DateFormatUtils.format(result.getTime6(), "yyyy-MM-dd HH:mm:ss"));

    }

    @Test
    public void testRate() throws Exception {
        Map<String, Object> jsonObject = new HashMap<>();
        double r = 123123.12345678d;
        jsonObject.put("rate", r);
        jsonObject.put("name", "point");
        String json = JSON.toJSONString(jsonObject);

        JavaType javaType = objectMapper.constructType(TestModel.class);
        JsonNode rootNode = objectMapper.readTree(json);
        JsonParser jsonParser = objectMapper.treeAsTokens(rootNode);
        TestModel dest = objectMapper.readValue(jsonParser, javaType);

        Assert.assertEquals("123123.1234", dest.getRate().toString());
    }

    @Test
    public void testDebeziumDecimal() {
        Map<String, Object> jsonObject = new HashMap<>(16);
        jsonObject.put("weight", "dGpSiAA=");
        jsonObject.put("score", "");

        String jsonStr = JSON.toJSONString(jsonObject);
        TestModel dest = JSON.parse(jsonStr, TestModel.class);
        Assert.assertEquals("5000000000.00", dest.getWeight().toString());
        Assert.assertNull(dest.getScore());
    }

    @Test
    public void testDebeziumDatetime() {
        long timeMs = 1619187379532L;
        Map<String, Object> jsonObject = new HashMap<>(16);
        jsonObject.put("timeCreated", timeMs);

        String jsonStr = JSON.toJSONString(jsonObject);
        TestModel dest = JSON.parse(jsonStr, TestModel.class);
        Assert.assertEquals("2021-04-23 22:16:19", DateFormatUtils.format(new Date(timeMs), "yyyy-MM-dd HH:mm:ss"));
        Assert.assertEquals("2021-04-23 14:16:19", DateFormatUtils.format(dest.getTimeCreated(), "yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    public void testPrettyFormat() {
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("username", "clive");
        jsonObject.put("age", 20);
        String noPrettyFormatJson = JSON.toJSONString(jsonObject);
        Assert.assertEquals("{\"age\":20,\"username\":\"clive\"}", noPrettyFormatJson);

        String prettyFormatJson = JSON.toJSONString(jsonObject, true);
        String line = System.getProperty("line.separator");
        Assert.assertEquals("{" + line +
                "  \"age\" : 20," + line +
                "  \"username\" : \"clive\"" + line +
                "}", prettyFormatJson);
    }

    @Test
    public void testNull() {
        String s = "{\"1\":123,\"2\":null}";
        JsonNode jsonNode = JSON.parse(s);
        //1节点存在
        Assert.assertNotNull(jsonNode.get("1"));
        Assert.assertFalse(jsonNode.get("1").isNull());

        //2节点存在，但是值为null，对应jsonnode类型为NullNode，它不是null
        Assert.assertNotNull(jsonNode.get("2"));
        Assert.assertNotNull(jsonNode.get("2").isNull());
        Assert.assertTrue(jsonNode.get("2") == NullNode.getInstance());
        Assert.assertEquals(jsonNode.get("2").asText(), "null");

        //不存在的3节点为null
        Assert.assertNull(jsonNode.get("3"));

        //下面演示JSON.of方法的使用
        int v1 = JSON.of(jsonNode.get("1")).map(JsonNode::asInt).orElse(0);
        Assert.assertEquals(123, v1);

        String v2 = JSON.of(jsonNode.get("2")).map(JsonNode::asText).orElse("defaultValue");
        Assert.assertEquals("defaultValue", v2);
    }

    static class TestFeature {
        private String hello;

        private List<Integer> nums;

        public String getHello() {
            return hello;
        }

        public void setHello(String hello) {
            this.hello = hello;
        }

        public List<Integer> getNums() {
            return nums;
        }

        public void setNums(List<Integer> nums) {
            this.nums = nums;
        }
    }

    @Test
    public void testFeature() {
        String text = "{ hello:\"1\", nums: 2}";
        TestFeature testFeature = JSON.parse(text, TestFeature.class);
        Assert.assertEquals("1", testFeature.hello);
        Assert.assertEquals(Integer.valueOf(2), testFeature.nums.get(0));
    }
}
