/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by August on 2021/8/31
 */
@RestController
@RequestMapping
@LogExclude
public class WebLogController2 {
    @RequestMapping(path = "/test")
    public ApiResponse<String> test(String id) {
        return ApiResponse.success("I am OK ...");
    }
}
