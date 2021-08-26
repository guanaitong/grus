/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.language;

import com.ciicgat.grus.language.LanguageConstant;
import com.ciicgat.grus.language.LanguageThreadLocal;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Locale;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/03/17 10:33
 * @Description:
 */
public class FeignLanguageInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        Locale locale = LanguageThreadLocal.getLocale();
        if (locale != null) {
            requestBuilder.addHeader(LanguageConstant.LANG_HEADER, locale.toLanguageTag());
        }

        return chain.proceed(requestBuilder.build());
    }
}
