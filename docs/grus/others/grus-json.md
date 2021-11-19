# JSON 使用说明

我们默认使用 jackson 库做 json 的处理。像 fastjson，因为漏洞太多了，大家不要使用。

为了使大家使用简单，我们提供了工具类和示例，所有示例在[单元测试](https://gitlab.wuxingdev.cn/java/framework/grus/blob/master/grus-core/src/test/java/com/ciicgat/grus/json/JSONTest.java)中都可以找到：

工具类为：`com.ciicgat.grus.json.JSON`

大家在迁移时，需要重点注意 date 类型的处理

尽量使用 class 来做映射，它能满足 90%以上的场景，不要使用 JsonNode。如果要用 JsonNode，请把文档和单元测试彻底理解通透后再用。

## 将对象输出为 JSON 字符串

```java
    /**
     * 将对象输出为json字符串
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }
```

使用起来很简单：

```java
        User user = new User(1, "a", new Date());
        String s = JSON.toJSONString(user);
        System.out.println(s);
        //通用场景，使用map或者list
        Map<String, Object> jsonObject = new LinkedHashMap<>();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "OK");
        jsonObject.put("data", true);
        System.out.println(JSON.toJSONString(jsonObject));
```

## 解析为指定的类

```java
/**
 * 解析为指定类
 *
 * @param text
 * @param clazz
 * @param <T>
 * @return
 */
public static <T> T parse(String text, Class<T> clazz) {
    try {
        return OBJECT_MAPPER.readValue(text, clazz);
    } catch (Throwable e) {
        throw new JSONException(e);
    }
}
```

使用示例：

```java
User u1 = JSON.parse(s, User.class);
```

## 处理泛型

上面的方法，如果遇到泛型嵌套时（比如说 List、Map），就无能为力了。可以使用下面方法：

```java
  /**
     * 用于处理带泛型嵌套的类
     *
     * @param text
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T parse(String text, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }
```

使用示例：

```java
        List<User> users = new ArrayList<>();
        users.add(new User(1, "a"));
        users.add(new User(2, "b"));
        String s = JSON.toJSONString(users);
        System.out.println(s);

        List<User> list = JSON.parse(s, new com.fasterxml.jackson.core.type.TypeReference<>() {
        });
        Assert.assertEquals(users, list);
```

## 转化为树结构 JsonNode

有时，大家不想写或者无法写一个类与 json 做映射，那么我们可以使用`JsonNode`

```java
    /**
     * 解析为通用的树的数据结构，不用和对象做映射
     *
     * @param text
     * @return
     */
    public static JsonNode parse(String text) {
        try {
            return OBJECT_MAPPER.readTree(text);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }
```

`JsonNode`常见的使用场景：

```java
        User user = new User(1, "a", new Date());
        String s = JSON.toJSONString(user);
        JsonNode jsonNode = JSON.parse(s);
        Assert.assertTrue(jsonNode.isObject());
        Assert.assertNotNull(jsonNode.get("id")); //取id节点
        Assert.assertNull(jsonNode.get("id1")); //不存在id1节点，所以返回为null
        Assert.assertEquals(jsonNode.get("id").asInt(), 1); //取id的值
        Assert.assertEquals(jsonNode.get("name").asText(), "a"); //取name的值
```

遍历`JsonNode`

```java
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        for (Iterator<Map.Entry<String, JsonNode>> it = fields; it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
```

当`JsonNode`是数组时：

```java
        List<User> users = new ArrayList<>();
        users.add(new User(1, "a"));
        users.add(new User(2, "b"));
        String s = JSON.toJSONString(users);
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
```

## JsonNode 为空时处理

JsonNode 本身可能为 null，还可能为 NullNode。下面演示这两种情况：

```java
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
```

大家可以使用`JSON.of`方法，处理 null 或者 NullNode 的情况，避免很多 if 判断：

```java
    /**
     * 方便处理jsonNode为NullNode情况
     *
     * @param jsonNode
     * @return
     */
    public static Optional<JsonNode> of(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return Optional.empty();
        }
        return Optional.of(jsonNode);
    }
```

下面演示 JSON.of 方法的使用：

```java

        int v1 = JSON.of(jsonNode.get("1")).map(JsonNode::asInt).orElse(0);
        Assert.assertEquals(123, v1);

        String v2 = JSON.of(jsonNode.get("2")).map(JsonNode::asText).orElse("defaultValue");
        Assert.assertEquals("defaultValue", v2);
```

## JsonNode 转化为 Java 对象

很多时候，大家在获取一个 jsonNode 的子 jsonNode 之后，可能有需求将其转换为对象。

可以使用`toJavaObject`处理：

```java
    /**
     * 用于将jsonNode转换为java对象
     *
     * @param jsonNode
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(JsonNode jsonNode, Class<T> tClass) {
        try {
            JsonParser jsonParser = OBJECT_MAPPER.treeAsTokens(jsonNode);
            return OBJECT_MAPPER.readValue(jsonParser, tClass);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * 用于将jsonNode转换为java对象
     *
     * @param jsonNode
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(JsonNode jsonNode, TypeReference<T> typeReference) {
        try {
            JsonParser jsonParser = OBJECT_MAPPER.treeAsTokens(jsonNode);
            return OBJECT_MAPPER.readValue(jsonParser, typeReference);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }
```
