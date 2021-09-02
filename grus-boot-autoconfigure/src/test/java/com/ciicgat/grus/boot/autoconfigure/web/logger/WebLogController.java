/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by August on 2021/8/31
 */
@RestController
@RequestMapping
public class WebLogController {
    @RequestMapping(path = "/test")
    public ApiResponse<String> test(String id, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        return ApiResponse.success("I am OK ...");
    }

    @RequestMapping(path = "/test1")
    @ResponseBody
    public ApiResponse<String> test1(String id, String id2) {
        return ApiResponse.success("I am OK2 ...");
    }

    @RequestMapping(path = "/test2")
    public void test2(String id, @LogExclude String id2, String id3) {
    }

    @RequestMapping(path = "/test3")
    public ApiResponse<String> test3(String id) {
        return ApiResponse.success("I am OK3 ...");
    }

    @RequestMapping(path = "/test4")
    @LogExclude
    public ApiResponse<String> test4(String id) {
        return ApiResponse.success("I am OK3 ...");
    }

    @RequestMapping(path = "/test5")
    public ApiResponse<String> test5(String id) {
        throw new RuntimeException();
    }

    @RequestMapping(path = "/isLive")
    public ApiResponse<String> isLive() {
        return ApiResponse.success("live");
    }
}
