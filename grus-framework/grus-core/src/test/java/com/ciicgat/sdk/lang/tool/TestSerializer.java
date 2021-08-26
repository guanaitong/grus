/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by August.Zhou on 2018-10-22 16:08.
 */
public class TestSerializer {


    public ForSerializerTest setUp() {
        ForSerializerTest forSerializerTest = new ForSerializerTest();
        forSerializerTest.setI(new Random().nextInt());
        forSerializerTest.setL(new Random().nextLong());
        forSerializerTest.setA(new SessionIdGenerator().generateSessionId(200));
        forSerializerTest.setB("ljlsadkjflksdaf09**2134");

        Map<String, String> map = new HashMap<>();
        map.put("kasjdfkasdjfkljds", "laksdjflkasdjf");
        map.put("kasjdfkasdjfasdf asdf asdkljds", new SessionIdGenerator().generateSessionId(200));
        map.put("asdf", "laksdjflkasdjf");
        map.put("asdfsdafdas f", "laksdjflkasdjf");
        forSerializerTest.setMap(map);

        Set<String> stringSet = new HashSet<>();
        stringSet.add("olksadfjlkasdjgjh");
        stringSet.add("olksadfjlkassda fasdf asdf djgjh");
        stringSet.add("asdfsda f adwsf asdf asdf");
        stringSet.add(new SessionIdGenerator().generateSessionId(200));
        forSerializerTest.setStringSet(stringSet);

        List<String> stringList = new ArrayList<>();
        stringList.add("olksadfjlkasdjgjh");
        stringList.add("olksadfjlkassda fasdf asdf djgjh");
        stringList.add("asdfsda f adwsf asdf asdf");
        stringList.add(new SessionIdGenerator().generateSessionId(200));
        forSerializerTest.setStringList(stringList);
        return forSerializerTest;
    }

    @Test
    public void testJava() {
        ForSerializerTest forSerializerTest = setUp();

        String forSerializerTestText = Serializer.JAVA.serialize(forSerializerTest);
        System.out.println("Serializer.JAVA length:" + forSerializerTestText.length());
        ForSerializerTest deserializeForSerializerTest = Serializer.JAVA.deserialize(forSerializerTestText, ForSerializerTest.class);
        Assert.assertEquals(forSerializerTest, deserializeForSerializerTest);


        byte[] forSerializerTestBytes = Serializer.JAVA.serializeToBytes(forSerializerTest);
        System.out.println("Serializer.JAVA length:" + forSerializerTestText.length());
        ForSerializerTest deserializeFromBytes = Serializer.JAVA.deserializeFromBytes(forSerializerTestBytes, ForSerializerTest.class);

        Assert.assertEquals(forSerializerTest, deserializeFromBytes);
    }

    @Test
    public void testJson() {

        ForSerializerTest forSerializerTest = setUp();

        String forSerializerTestText = Serializer.JSON.serialize(forSerializerTest);
        System.out.println("Serializer.JSON length:" + forSerializerTestText.length());
        ForSerializerTest deserializeForSerializerTest = Serializer.JSON.deserialize(forSerializerTestText, ForSerializerTest.class);
        Assert.assertEquals(forSerializerTest, deserializeForSerializerTest);

        byte[] forSerializerTestBytes = Serializer.JSON.serializeToBytes(forSerializerTest);
        System.out.println("Serializer.JAVA length:" + forSerializerTestText.length());
        ForSerializerTest deserializeFromBytes = Serializer.JSON.deserializeFromBytes(forSerializerTestBytes, ForSerializerTest.class);
        Assert.assertEquals(forSerializerTest, deserializeFromBytes);
    }


    private static class ForSerializerTest implements Serializable {
        private int i;
        private long l;
        private String a;
        private String b;
        private Map<String, String> map;
        private List<String> stringList;
        private Set<String> stringSet;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public long getL() {
            return l;
        }

        public void setL(long l) {
            this.l = l;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        public List<String> getStringList() {
            return stringList;
        }

        public void setStringList(List<String> stringList) {
            this.stringList = stringList;
        }

        public Set<String> getStringSet() {
            return stringSet;
        }

        public void setStringSet(Set<String> stringSet) {
            this.stringSet = stringSet;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ForSerializerTest)) return false;

            ForSerializerTest that = (ForSerializerTest) o;

            if (i != that.i) return false;
            if (l != that.l) return false;
            if (a != null ? !a.equals(that.a) : that.a != null) return false;
            if (b != null ? !b.equals(that.b) : that.b != null) return false;
            if (map != null ? !map.equals(that.map) : that.map != null) return false;
            if (stringList != null ? !stringList.equals(that.stringList) : that.stringList != null) return false;
            return stringSet != null ? stringSet.equals(that.stringSet) : that.stringSet == null;
        }

        @Override
        public int hashCode() {
            int result = i;
            result = 31 * result + (int) (l ^ (l >>> 32));
            result = 31 * result + (a != null ? a.hashCode() : 0);
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (map != null ? map.hashCode() : 0);
            result = 31 * result + (stringList != null ? stringList.hashCode() : 0);
            result = 31 * result + (stringSet != null ? stringSet.hashCode() : 0);
            return result;
        }
    }
}
