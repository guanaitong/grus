/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2017/8/3 10:03.
 */
public class TestApacheHttpClientSingleton {

    @Test
    public void test() {
        CloseableHttpClient closeableHttpClient = HttpClientSingleton.getApacheHttpClient();

        HttpGet httpGet = new HttpGet("http://www.sina.com.cn/");
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        CloseableHttpClient closeableHttpClient = HttpClientSingleton.getApacheHttpClient();

        HttpGet httpGet = new HttpGet("https://www.baidu.com/");
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        CloseableHttpClient closeableHttpClient = HttpClientSingleton.getApacheHttpClient();

        HttpGet httpGet = new HttpGet("https://www.baidu.com/");
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() {
        CloseableHttpClient closeableHttpClient = HttpClientSingleton.getApacheHttpClient();

        HttpGet httpGet = new HttpGet("https://passport.guanaitong.com");
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4() {
        String contentUsingGet = ApacheHttpClientHelper.get("https://passport.guanaitong.com", null);
    }

    @Test
    public void test5() {
        HttpClientSingleton.setTrustAny(true);
        String s = ApacheHttpClientHelper.get("https://aspsnet.sf-express.com/extranet/meal/orderManager/queryBalance.pub");
        System.out.println(s);
    }


}
