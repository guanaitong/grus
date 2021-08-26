/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import com.ciicgat.api.core.annotation.ServiceName;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Created by August.Zhou on 2019-03-06 15:28.
 */
@ServiceName(value = "userdoor", urlPathPrefix = "/userdoor")
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface PersonService {


    /**
     * 根据id获取个人信息
     *
     * @param personId 主键
     * @return
     */
    @RequestLine("POST /person/get")
    Person getPersonById(@Param("id") Integer personId);

}
