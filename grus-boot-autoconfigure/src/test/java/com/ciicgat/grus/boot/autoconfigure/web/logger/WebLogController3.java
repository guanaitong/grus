/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import com.ciicgat.sdk.lang.convert.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by August on 2021/8/31
 */
@Controller
@RequestMapping
@EnableGrusWebLogJob
public class WebLogController3 {
    @RequestMapping(path = "/test")
    public ApiResponse<String> test(String id) {
        return ApiResponse.success("I am OK ...");
    }
}
