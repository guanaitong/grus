/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * Created by Josh on 17-11-9.
 */
public class ValidateTest {

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Test
    public void testSuccess() throws Exception {
        TestMethod testMethod = new TestMethod();
        testMethod.setValue("12", 2L, null);

        TestBean testBean = new TestBean();
        testBean.setTestString("123123");
        testBean.setSortMode("ID_ASC");
        testBean.setTestLong(3L);

        Method declaredMethod = getMethod();

        MethodValidator methodValidator = new MethodValidator(declaredMethod, parameterNameDiscoverer.getParameterNames(declaredMethod));

        ValidateResult validateResult = methodValidator.validate(new Object[]{"12", 2L, testBean});

        Assert.assertTrue(validateResult.isValid());

    }

    private Method getMethod() {
        Method[] declaredMethods = TestMethod.class.getDeclaredMethods();
        Method declaredMethod = declaredMethods[0];

        for (Method method : declaredMethods) {
            if (!method.isSynthetic()) {
                declaredMethod = method;
                break;
            }
        }
        return declaredMethod;
    }

    @Test
    public void testFail() throws Exception {
        TestMethod testMethod = new TestMethod();
        testMethod.setValue("12", 2L, null);

        TestBean testBean = new TestBean();
        testBean.setTestString("123123");
        testBean.setSortMode("ID_ASC");

        Method declaredMethod = getMethod();
        MethodValidator methodValidator = new MethodValidator(declaredMethod, parameterNameDiscoverer.getParameterNames(declaredMethod));

        ValidateResult validateResult = methodValidator.validate(new Object[]{null, 2L, testBean});
        Assert.assertTrue(!validateResult.isValid());
    }

    @Test
    public void testFail2() throws Exception {
        TestMethod testMethod = new TestMethod();
        testMethod.setValue("12", 2L, null);

        TestBean testBean = new TestBean();
        testBean.setTestString("123123");
        testBean.setSortMode("1");

        Method declaredMethod = getMethod();
        MethodValidator methodValidator = new MethodValidator(declaredMethod, parameterNameDiscoverer.getParameterNames(declaredMethod));

        ValidateResult validateResult = methodValidator.validate(new Object[]{"12", 0L, testBean});
        Assert.assertTrue(!validateResult.isValid());
    }

//    @Test
//    public void testPrimitiveNotNullFail1() {
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPrimitive")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e.getCause() instanceof ValidateRuntimeException);
//            ValidateRuntimeException ex = (ValidateRuntimeException) e.getCause();
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    @Test
//    public void testPrimitiveNotNullFail2() {
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPrimitive?testString=12")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e.getCause() instanceof ValidateRuntimeException);
//            ValidateRuntimeException ex = (ValidateRuntimeException) e.getCause();
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    @Test
//    public void testPrimitiveMinSuccess() {
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPrimitive?testString=1&testLong=2")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//        } catch (Exception e) {
//            Assert.fail();
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testPrimitiveMinFail() {
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPrimitive?testString=1&testLong=0")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e.getCause() instanceof ValidateRuntimeException);
//            ValidateRuntimeException ex = (ValidateRuntimeException) e.getCause();
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    @Test
//    public void testPojoNotNullSuccess() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/testPojo?testString=12&testLong=1")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//    }
//
//    @Test
//    public void testPojoNotNullFail1() throws Exception {
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPojo")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e.getCause() instanceof ValidateRuntimeException);
//            ValidateRuntimeException ex = (ValidateRuntimeException) e.getCause();
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    @Test
//    public void testPojoNotNullFail2() throws Exception {
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPojo?testString=1")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e.getCause() instanceof ValidateRuntimeException);
//            ValidateRuntimeException ex = (ValidateRuntimeException) e.getCause();
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    @Test
//    public void testPojoNotNullFail3() throws Exception {
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPojo?testString=1&testLong=0")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e.getCause() instanceof ValidateRuntimeException);
//            ValidateRuntimeException ex = (ValidateRuntimeException) e.getCause();
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    @Test
//    public void testPojoInEnumFail() throws Exception {
//
//        try {
//            mockMvc.perform(MockMvcRequestBuilders.post("/testPojo?testString=1&testLong=1&invoiceStatus=9")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andDo(MockMvcResultHandlers.print())
//                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("success")));
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e.getCause() instanceof ValidateRuntimeException);
//            ValidateRuntimeException ex = (ValidateRuntimeException) e.getCause();
//            System.out.println(ex.getMessage());
//        }
//    }
}
