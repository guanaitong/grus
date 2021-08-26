/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.BodyBean;
import com.ciicgat.api.core.model.DateBean;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.model.BodyBean2;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * Created by August.Zhou on 2017/7/31 10:36.
 */
@ServiceName("post-put")
public interface HttpPostPutService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /post")
    TestBean post(@Param("text") String text, @Param("integer") int integer);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /postWithUrlParams?text={text}")
    TestBean postWithUrlParams(@Param("text") String text, @Param("integer") int integer);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /postBean")
    TestBean postBean(BodyBean bodyBean);


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /postBean2")
    TestBean postBean2(BodyBean2 bodyBean2);


    @Headers("Content-Type: application/json")
    @RequestLine("POST /postJson")
    TestBean postJson(BodyBean bodyBean);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /postJsonWithUrlParams?text={text}")
    TestBean postJsonWithUrlParams(@Param("text") String text, BodyBean bodyBean);


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("PUT /put")
    TestBean put(@Param("text") String text, @Param("integer") int integer);


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /postWithListParams")
    TestBean postWithListParams(@Param("texts") List<String> texts, @Param("integer") int integer);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /requestWithDate")
    TestBean requestWithDate(DateBean dateBean);

}
