/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by August.Zhou on 2017/8/3 10:03.
 */
public class TestOkHttpClientSingleton {

    @Test
    public void test() {
        OkHttpClient okHttpClient = HttpClientSingleton.getOkHttpClient();


        Request request = new Request.Builder().get().url("http://www.sina.com.cn/").build();
        try {
            okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        OkHttpClient okHttpClient = HttpClientSingleton.getOkHttpClient();


        Request request = new Request.Builder().get().url("https://www.baidu.com/s?wd=123&rsv_spt=1&rsv_iqid=0xcdebf46600095d4f&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=3&rsv_sug1=2&rsv_sug7=100&rsv_sug2=0&inputT=508&rsv_sug4=508").build();
        try {
            okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        request = new Request.Builder().get().url("https://www.baidu.com/s?wd=123&rsv_spt=1&rsv_iqid=0xcdebf46600095d4f&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=3&rsv_sug1=2&rsv_sug7=100&rsv_sug2=0&inputT=508&rsv_sug4=508").build();
        try {
            okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        OkHttpClient okHttpClient = HttpClientSingleton.getOkHttpClient();


        Request request = new Request.Builder().get().url("https://passport.guanaitong.com").build();
        try {
            okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    @Test
//    public void test3() {
//        OkHttpClient okHttpClient = HttpClientSingleton.getOkHttpClient();
//
//
//        Request request = new Request.Builder().get().url("http://10.101.9.205/").build();
//        try {
//            okHttpClient.newCall(request).request().body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void test4() {
        String contentUsingGet = HttpClientHelper.get("https://passport.guanaitong.com");
    }
}
