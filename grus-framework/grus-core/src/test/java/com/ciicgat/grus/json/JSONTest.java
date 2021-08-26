/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(user, u1);

        User u2 = JSON.parse(s, new TypeReference<>() {
        });
        Assert.assertEquals(u2, user);

        JsonNode jsonNode = JSON.parse(s);
        Assert.assertTrue(jsonNode.isObject());
        Assert.assertNotNull(jsonNode.get("id")); //取id节点
        Assert.assertNull(jsonNode.get("id1")); //不存在id1节点，所以返回为null
        Assert.assertEquals(jsonNode.get("id").asInt(), 1); //取id的值
        Assert.assertEquals(jsonNode.get("name").asText(), "a"); //取name的值

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
        Assert.assertEquals(new User(1, null, new Date()), u1);
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
        Assert.assertEquals(users, list);

        JsonNode jsonNode = JSON.parse(s);
        Assert.assertTrue(jsonNode.isArray());

        //通过迭代器遍历
        for (Iterator<JsonNode> it = jsonNode.iterator(); it.hasNext(); ) {
            JsonNode node = it.next();
            JsonNode idJsonNode = node.get("id");
            JsonNode nameJsonNode = node.get("name");
            Assert.assertTrue(idJsonNode.isInt());
            Assert.assertTrue(nameJsonNode.isTextual());
        }

        //通过index遍历
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode node = jsonNode.get(i);
            JsonNode idJsonNode = node.get("id");
            JsonNode nameJsonNode = node.get("name");
            Assert.assertTrue(idJsonNode.isInt());
            Assert.assertTrue(nameJsonNode.isTextual());
        }
    }

    @Test
    public void testJavaType() {
        String t = "[1,2,3]";
        JavaType javaType = JSON.getTypeFactory().constructParametricType(List.class, Integer.class);
        List<Integer> list1 = JSON.parse(t, javaType);
        List<Integer> list2 = JSON.parse(t, new TypeReference<>() {
        });
        Assert.assertEquals(list1, list2);
    }
}
