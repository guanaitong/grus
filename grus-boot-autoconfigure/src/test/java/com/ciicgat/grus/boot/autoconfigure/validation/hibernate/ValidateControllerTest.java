/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.hibernate;

import com.ciicgat.grus.json.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * @author wanchongyang
 * @date 2020/5/9 4:55 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HibernateValidationApplication.class,
        properties = {"spring.application.name=grus-demo", "grus.hibernate.validator.enabled=true"})
@AutoConfigureMockMvc
public class ValidateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test(expected = ConstraintViolationException.class)
    public void testRequestParams() throws Throwable {
        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/validate/testRequestParams")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        try {
            mockMvc.perform(post);
        } catch (Exception ex) {
            throw ex.getCause();
        }
    }

    @Test
    public void testValidateService() {
        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/validate/testValidateService")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        try {
            mockMvc.perform(post);
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof ConstraintViolationException);
            Assert.assertTrue(ex.getMessage().contains("null"));
        }
    }

    @Test
    public void testFormData() throws Exception {
        // 参数无效，验证失败
        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/validate/testFormData")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse response = mockMvc.perform(post).andReturn().getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        Assert.assertTrue(content.contains("personId"));
        Assert.assertTrue(content.contains("null"));

        List<String> errorMsgList = JSON.parse(content, new TypeReference<>() {
        });
        Assert.assertEquals(1, errorMsgList.size());

        // 参数有效，验证通过
        post = MockMvcRequestBuilders.post("/validate/testFormData")
                .param("personId", "1")
                .param("personName", "我是张三，员工姓名")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        response = mockMvc.perform(post).andReturn().getResponse();
        response.setCharacterEncoding("UTF-8");
        content = response.getContentAsString();
        Assert.assertEquals("[]", content);

        errorMsgList = JSON.parse(content, new TypeReference<>() {
        });
        Assert.assertTrue(errorMsgList.isEmpty());
    }

    @Test
    public void testRequestBody() throws Exception {
        PersonRequest personRequest = new PersonRequest();
        personRequest.setPersonId(10);
        personRequest.setPersonName("我是李四，张三是我兄弟");

        PersonPlusRequest personPlusRequest = new PersonPlusRequest();
        personPlusRequest.setPortrait("头像");
        personRequest.setPersonPlusRequest(personPlusRequest);

        // 参数无效，级联验证失败
        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/validate/testRequestBody")
                .content(JSON.toJSONString(personRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse response = mockMvc.perform(post).andReturn().getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        Assert.assertTrue(content.contains("personPlusRequest.portrait"));
        Assert.assertTrue(content.contains("URL"));

        List<String> errorMsgList = JSON.parse(content, new TypeReference<>() {
        });
        Assert.assertEquals(1, errorMsgList.size());
    }
}
