/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.serializer;

import com.ciicgat.sdk.lang.tool.Serializer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by August.Zhou on 2017/1/3 16:31.
 */
public class TestSerializer {


    private static ForSerializerTest forSerializerTest = new ForSerializerTest();

    @BeforeClass
    public static void setUp() {

        forSerializerTest.setI(1298312893);
        forSerializerTest.setL(1982739812738929874L);
        forSerializerTest.setA("sal;djflkasdjf;lkjsdal;kfj");
        forSerializerTest.setB("ljlsadkjflksdaf09**2134");

        Map<String, String> map = new HashMap<>();
        map.put("kasjdfkasdjfkljds", "laksdjflkasdjf");
        map.put("kasjdfkasdjfasdf asdf asdkljds", "laksdjflkasdjf");
        map.put("asdf", "laksdjflkasdjf");
        map.put("asdfsdafdas f", "laksdjflkasdjf");
        forSerializerTest.setMap(map);

        Set<String> stringSet = new HashSet<>();
        stringSet.add("olksadfjlkasdjgjh");
        stringSet.add("olksadfjlkassda fasdf asdf djgjh");
        stringSet.add("asdfsda f adwsf asdf asdf");
        stringSet.add("olksadfjlka3244f35asd4fr())*98123&^^djgjh");
        forSerializerTest.setStringSet(stringSet);

        List<String> stringList = new ArrayList<>();
        stringList.add("olksadfjlkasdjgjh");
        stringList.add("olksadfjlkassda fasdf asdf djgjh");
        stringList.add("asdfsda f adwsf asdf asdf");
        stringList.add("olksadfjlka3244f35asd4fr())*98123&^^djgjh");
        forSerializerTest.setStringList(stringList);

    }

    @Test
    public void testJava() {


        String forSerializerTestText = Serializer.JAVA.serialize(forSerializerTest);
        System.out.println("Serializer.JAVA length:" + forSerializerTestText.length());
        ForSerializerTest deserializeForSerializerTest = Serializer.JAVA.deserialize(forSerializerTestText, ForSerializerTest.class);
        assertForSerializerTest(deserializeForSerializerTest);
    }

    @Test
    public void testJson() {

        String forSerializerTestText = Serializer.JSON.serialize(forSerializerTest);
        System.out.println("Serializer.JSON length:" + forSerializerTestText.length());
        ForSerializerTest deserializeForSerializerTest = Serializer.JSON.deserialize(forSerializerTestText, ForSerializerTest.class);
        assertForSerializerTest(deserializeForSerializerTest);
    }


    private void assertForSerializerTest(ForSerializerTest forSerializerTestDe) {
        Assert.assertEquals(forSerializerTest, forSerializerTestDe);
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
