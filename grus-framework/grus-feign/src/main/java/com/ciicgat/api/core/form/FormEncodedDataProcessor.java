/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.form;

import com.ciicgat.sdk.lang.constant.GatDateFormat;
import com.ciicgat.sdk.lang.url.UrlCoder;
import com.google.common.primitives.Bytes;
import feign.Request;
import feign.RequestTemplate;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Form urlencoded implementation of {@link FormDataProcessor}.
 *
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 30.04.2016
 */
class FormEncodedDataProcessor implements FormDataProcessor {

    public static final String CONTENT_TYPE;
    static final Charset UTF_8 = Charset.forName("UTF-8");

    static {
        CONTENT_TYPE = "application/x-www-form-urlencoded";
    }

    @Override
    public void process(Map<String, Object> data, RequestTemplate template) {
        StringBuilder body = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (body.length() > 0) {
                body.append('&');
            }
            body.append(createKeyValuePair(entry));
        }

        template.header("Content-Type", CONTENT_TYPE);
        String string = body.toString();
        byte[] bytes2 = template.requestBody().asBytes();

        if (string.isEmpty()) {
            return;
        }

        if (bytes2 == null || bytes2.length == 0) {
            template.body(Request.Body.encoded(string.getBytes(UTF_8), UTF_8));
            return;
        }

        /**
         * 这边拼接原有的apiName等参数
         */
        body.append("&");
        byte[] bytes1 = com.ciicgat.sdk.lang.tool.Bytes.toBytes(body.toString());

//        byte[] bytes3 = new byte[bytes1.length + bytes2.length];
//        System.arraycopy(bytes1, 0, bytes3, 0, bytes1.length);
//        System.arraycopy(bytes2, 0, bytes3, bytes1.length, bytes2.length);

        template.body(Request.Body.encoded(Bytes.concat(bytes1, bytes2), UTF_8));
    }

    @Override
    public String getSupportetContentType() {
        return CONTENT_TYPE;
    }

    private String createKeyValuePair(Map.Entry<String, Object> entry) {
        String value;
        if (entry.getValue() instanceof Collection) {
            Collection collection = (Collection) entry.getValue();
            StringJoiner joiner = new StringJoiner(",");
            for (Object cs : collection) {
                joiner.add(cs.toString());
            }
            value = joiner.toString();
        } else if (entry.getValue() instanceof Date) {
            value = DateFormatUtils.format((Date) entry.getValue(), GatDateFormat.FULL_PATTERN);
        } else {
            value = entry.getValue().toString();
        }
        return new StringBuilder()
                .append(UrlCoder.encode(entry.getKey()))
                .append('=')
                .append(UrlCoder.encode(value))
                .toString();
    }
}
