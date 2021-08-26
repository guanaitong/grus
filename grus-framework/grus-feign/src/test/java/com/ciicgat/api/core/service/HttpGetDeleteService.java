/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.Pagination;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * 测试HTTP的GET和DELETE
 * Created by August.Zhou on 2017/7/31 10:36.
 */
@ServiceName("get-delete")
public interface HttpGetDeleteService {

    @RequestLine("GET /get/{serverId}?count={count}")
    TestBean get(@Param("serverId") String serverId, @Param("count") int count);


    @RequestLine("GET /getUnNormal")
    TestBean getUnNormal(@Param("serverId") String serverId, @Param("count") int count);

    @RequestLine("DELETE /delete/{serverId}?count={count}")
    TestBean delete(@Param("serverId") String serverId, @Param("count") int count);

    //测试返回结果为code、msg、data时，code=0，自动去掉code/msg/data包装
    @RequestLine("GET /getWithApiRespData")
    TestBean getWithApiRespData();

    @RequestLine("GET /getWithApiRespDataCodeNotZero")
    TestBean getWithApiRespDataCodeNotZero();

    @RequestLine("GET /getBeanList")
    List<TestBean> getBeanList();


    @RequestLine("GET /getApiResponseOfBeanList")
    ApiResponse<List<TestBean>> getApiResponseOfBeanList();

    @RequestLine("GET /getBeanListWithApiRespData")
    List<TestBean> getBeanListWithApiRespData();


    @RequestLine("GET /getBeanPagination")
    Pagination<TestBean> getBeanPagination();


    @RequestLine("GET /get?serverIds={serverIds}&count={count}")
    TestBean getWithListParams(@Param("serverIds") List<String> serverIdList, @Param("count") int count);

}
