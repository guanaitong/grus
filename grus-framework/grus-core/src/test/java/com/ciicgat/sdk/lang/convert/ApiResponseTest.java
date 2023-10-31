/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by August.Zhou on 2023/10/31 14:10
 */
class ApiResponseTest {

    @Test
    void getOrThrow() {
        ApiResponse<Integer> apiResponse = ApiResponse.success(1);
        assertEquals(1, apiResponse.getOrThrow());

        ApiResponse<Integer> apiResponse2 = ApiResponse.fail(1, "failed");
        assertThrows(BusinessRuntimeException.class, apiResponse2::getOrThrow);
    }

    @Test
    void test2String() {
        ApiResponse<Integer> apiResponse = ApiResponse.success(1);
        assertEquals("{\"code\":0,\"msg\":\"OK\",\"data\":1}", apiResponse.toString());
        ApiResponse<Integer> apiResponse2 = ApiResponse.fail(1, "failed");
        assertEquals("{\"code\":1,\"msg\":\"failed\"}", apiResponse2.toString());
    }
}
