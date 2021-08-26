/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.BodyBean;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.model.DateBean;
import com.ciicgat.api.core.model.MvcDateBeanRequest;
import com.ciicgat.api.core.model.MvcDateBeanResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-05-04 16:27
 */
@ServiceName("mvc-post")
@RequestMapping("/mvc-post")
public interface HttpMvcPostService {

    @PostMapping(value = "/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TestBean postBean(BodyBean bodyBean);

    @PostMapping(value = "/postJson", consumes = MediaType.APPLICATION_JSON_VALUE)
    TestBean postJson(@RequestBody BodyBean bodyBean);

    /**
     * 测试 form 情况下，date 会转成 2010-12-12 12:12:12 这种格式字符串
     * 这个是 FormEncodedDataProcessor 里强制转换的
     *
     * @param dateBean dateBean
     * @return TestBean
     */
    @PostMapping(value = "/formRequestWithDate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TestBean requestWithDate(DateBean dateBean);

    /**
     * json 格式，date 不会强制转格式，转成 json 格式默认是时间戳
     *
     * @param dateBean dateBean
     * @return TestBean
     */
    @PostMapping(value = "/jsonRequestWithDate", consumes = MediaType.APPLICATION_JSON_VALUE)
    TestBean jsonRequestWithDate(@RequestBody DateBean dateBean);

    @PostMapping(value = "/jsonWithDate", consumes = MediaType.APPLICATION_JSON_VALUE)
    MvcDateBeanResponse jsonWithDate(@RequestBody MvcDateBeanRequest mvcDateBeanRequest);

    @PostMapping("/formString")
    String valueString(BodyBean bodyBean);

}
