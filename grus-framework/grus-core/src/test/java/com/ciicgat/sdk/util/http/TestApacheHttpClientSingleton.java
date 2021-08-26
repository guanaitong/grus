/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
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
