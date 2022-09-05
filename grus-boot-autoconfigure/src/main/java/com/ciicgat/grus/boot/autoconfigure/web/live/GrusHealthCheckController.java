/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.live;

import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-11-12 17:16
 */
@RestController
@RequestMapping
public class GrusHealthCheckController {

    @Autowired
    private GrusHealthChecker grusHealthChecker;

    @LogExclude
    @GetMapping("/isLive")
    public ApiResponse<String> isLive() {
        return ApiResponse.success(grusHealthChecker.isLive());
    }

}
