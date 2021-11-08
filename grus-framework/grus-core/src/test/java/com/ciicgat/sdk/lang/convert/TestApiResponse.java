/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import com.ciicgat.grus.json.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2017/4/28 11:49.
 */
public class TestApiResponse {

    @Test
    public void test() {
        Map<String, Object> jsonObject = new LinkedHashMap<>();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "OK");
        jsonObject.put("data", true);


        System.out.println(JSON.toJSONString(jsonObject));
        System.out.println(ApiResponse.success(true).toString());
        Assertions.assertEquals(JSON.toJSONString(jsonObject), ApiResponse.success(true).toString());

        JsonNode jsonObject1 = JSON.parse(ApiResponse.fail(10, "xxx").toString());
        Assertions.assertEquals(jsonObject1.get("code").asInt(), 10);
        Assertions.assertEquals(jsonObject1.get("msg").asText(), "xxx");


        JsonNode jsonObject2 = JSON.parse(ApiResponse.fail(new BaseErrorCode(10, "xxx")).toString());
        Assertions.assertEquals(jsonObject2.get("code").asInt(), 10);
        Assertions.assertEquals(jsonObject2.get("msg").asText(), "xxx");

        JsonNode jsonObject3 = JSON.parse(ApiResponse.fail(new BaseErrorCode(10, "xxx"), "datadata").toString());
        Assertions.assertEquals(jsonObject3.get("code").asInt(), 10);
        Assertions.assertEquals(jsonObject3.get("msg").asText(), "xxx");
        Assertions.assertEquals(jsonObject3.get("data").asText(), "datadata");

        ApiResponse<String> fail = ApiResponse.fail(new BaseErrorCode(10, "xxx"), "datadata");
        ApiResponse<String> fail2 = ApiResponse.fail(new BaseErrorCode(10, "xxx"), "datadata");

        Assertions.assertEquals(fail, fail2);
        Assertions.assertEquals(fail.hashCode(), fail2.hashCode());
    }


}
