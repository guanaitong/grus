/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;

import com.ciicgat.grus.json.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(JSON.toJSONString(jsonObject), ApiResponse.success(true).toString());

        JsonNode jsonObject1 = JSON.parse(ApiResponse.fail(10, "xxx").toString());
        Assert.assertEquals(jsonObject1.get("code").asInt(), 10);
        Assert.assertEquals(jsonObject1.get("msg").asText(), "xxx");


        JsonNode jsonObject2 = JSON.parse(ApiResponse.fail(new BaseErrorCode(10, "xxx")).toString());
        Assert.assertEquals(jsonObject2.get("code").asInt(), 10);
        Assert.assertEquals(jsonObject2.get("msg").asText(), "xxx");

        JsonNode jsonObject3 = JSON.parse(ApiResponse.fail(new BaseErrorCode(10, "xxx"), "datadata").toString());
        Assert.assertEquals(jsonObject3.get("code").asInt(), 10);
        Assert.assertEquals(jsonObject3.get("msg").asText(), "xxx");
        Assert.assertEquals(jsonObject3.get("data").asText(), "datadata");

        ApiResponse<String> fail = ApiResponse.fail(new BaseErrorCode(10, "xxx"), "datadata");
        ApiResponse<String> fail2 = ApiResponse.fail(new BaseErrorCode(10, "xxx"), "datadata");

        Assert.assertEquals(fail, fail2);
        Assert.assertEquals(fail.hashCode(), fail2.hashCode());
    }


}
