/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author : August
 * @date 2020/3/23 14:20
 */
public class JSONTest {

    public static class User {
        private int id;
        private String name;
        private Date date;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public User(int id, String name, Date date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public User() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return id == user.id &&
                    Objects.equals(name, user.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    @Test
    public void testObject() {
        User user = new User(1, "a", new Date());
        String s = JSON.toJSONString(user);
        System.out.println(s);

        User u1 = JSON.parse(s, User.class);
        Assertions.assertEquals(user, u1);

        User u2 = JSON.parse(s, new TypeReference<>() {
        });
        Assertions.assertEquals(u2, user);

        JsonNode jsonNode = JSON.parse(s);
        Assertions.assertTrue(jsonNode.isObject());
        Assertions.assertNotNull(jsonNode.get("id")); //取id节点
        Assertions.assertNull(jsonNode.get("id1")); //不存在id1节点，所以返回为null
        Assertions.assertEquals(jsonNode.get("id").asInt(), 1); //取id的值
        Assertions.assertEquals(jsonNode.get("name").asText(), "a"); //取name的值

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        for (Iterator<Map.Entry<String, JsonNode>> it = fields; it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
    }

    @Test
    public void testNull() {
        String s = "{\n" +
                "\t\"id\": 1,\n" +
                "\t\"name\": null,\n" +
                "\t\"date\": 1586959364189\n" +
                "}";
        User u1 = JSON.parse(s, User.class);
        Assertions.assertEquals(new User(1, null, new Date()), u1);
    }

    @Test
    public void testArray() {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "a"));
        users.add(new User(2, "b"));
        String s = JSON.toJSONString(users);
        System.out.println(s);

        List<User> list = JSON.parse(s, new TypeReference<List<User>>() {
        });
        Assertions.assertEquals(users, list);

        JsonNode jsonNode = JSON.parse(s);
        Assertions.assertTrue(jsonNode.isArray());

        //通过迭代器遍历
        for (Iterator<JsonNode> it = jsonNode.iterator(); it.hasNext(); ) {
            JsonNode node = it.next();
            JsonNode idJsonNode = node.get("id");
            JsonNode nameJsonNode = node.get("name");
            Assertions.assertTrue(idJsonNode.isInt());
            Assertions.assertTrue(nameJsonNode.isTextual());
        }

        //通过index遍历
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode node = jsonNode.get(i);
            JsonNode idJsonNode = node.get("id");
            JsonNode nameJsonNode = node.get("name");
            Assertions.assertTrue(idJsonNode.isInt());
            Assertions.assertTrue(nameJsonNode.isTextual());
        }
    }

    @Test
    public void testJavaType() {
        String t = "[1,2,3]";
        JavaType javaType = JSON.getTypeFactory().constructParametricType(List.class, Integer.class);
        List<Integer> list1 = JSON.parse(t, javaType);
        List<Integer> list2 = JSON.parse(t, new TypeReference<>() {
        });
        Assertions.assertEquals(list1, list2);
    }

    @Test
    public void deepEquals() {
        Assert.assertTrue(JSON.equals(null, null));
        Assert.assertTrue(JSON.equals(1, 1));
        Assert.assertTrue(JSON.equals(1L, 1L));
        Assert.assertTrue(JSON.equals(Integer.valueOf(123), 123));
        Assert.assertTrue(JSON.equals(Integer.valueOf(123), 123));
        Assert.assertTrue(JSON.equals(true, true));
        Assert.assertTrue(JSON.equals('a', 'a'));
        Assert.assertTrue(JSON.equals("adsfasdf", "adsfasdf"));
        Assert.assertFalse(JSON.equals(true, null));
        Assert.assertFalse(JSON.equals(null, true));
        System.out.println(JSON.toJSONString(1));
        System.out.println(JSON.toJSONString(true));
        System.out.println(JSON.toJSONString("adsfasdf"));
        ArrayList<String> arrayListA = new ArrayList<>();
        arrayListA.add("123");
        arrayListA.add("1231");
        arrayListA.add("1232");

        ArrayList<String> arrayListB = new ArrayList<>();
        arrayListB.add("123");
        arrayListB.add("1231");
        arrayListB.add("1232");

        Assert.assertTrue(JSON.equals(arrayListA, arrayListB));

        ArrayList<String> arrayListC = new ArrayList<>();
        arrayListC.add("123xx");
        arrayListC.add("1231");
        arrayListC.add("1232");
        Assert.assertFalse(JSON.equals(arrayListA, arrayListC));

        System.out.println(JSON.toJSONString(arrayListA));

        Object[] arrayA = new Object[]{1, true, "123", arrayListA};

        Object[] arrayB = new Object[]{1, true, "123", arrayListB};
        Assert.assertTrue(JSON.equals(arrayA, arrayB));

        Map<String, Object> mapA = Map.of("123", true, "445", 123, "789", arrayA, "7890", arrayListA);

        TestB testB1 = new TestB("123", 123, arrayListA, mapA);

        Map<String, Object> mapB = Map.of("123", true, "445", 123, "789", arrayB, "7890", arrayListA);
        TestB testB2 = new TestB("123", 123, arrayListB, mapB);

        Assert.assertTrue(JSON.equals(testB1, testB2));

        TestB testB3 = new TestB("123", 1234, arrayListB, mapB);

        Assert.assertFalse(JSON.equals(testB1, testB3));


        ArrayList<Object> arrayListAA = new ArrayList<>();
        arrayListAA.add(testB1);
        arrayListAA.add(testB2);

        ArrayList<Object> arrayListBB = new ArrayList<>();
        arrayListBB.add(testB2);
        arrayListBB.add(testB1);

        Assert.assertTrue(JSON.equals(arrayListAA, arrayListBB));

    }

    public static class TestB {
        private String s;
        private Integer i;
        private List<String> list;
        private Map<String, Object> map;

        public TestB(String s, Integer i, List<String> list, Map<String, Object> map) {
            this.s = s;
            this.i = i;
            this.list = list;
            this.map = map;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }
    }
}
