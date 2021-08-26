/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.validate;

import com.ciicgat.boot.validator.annotation.Min;
import com.ciicgat.boot.validator.annotation.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Josh on 17-11-9.
 */
@RestController
public class TestController {

    @RequestMapping("/testPrimitive")
    public String testPrimitive(@NotNull String testString, @NotNull @Min(1) Long testLong) {
        return "success";
    }

    @RequestMapping("/testPojo")
    public String testPojo(Test test) {
        return "success";
    }
}
