/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.url.UrlCoder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 将ApiResponse的code、msg加到返回的header中
 * Created by August.Zhou on 2019-05-28 19:05.
 */
@ControllerAdvice
public class ApiResponseHeaderAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse apiResponse) {
            attachHeader(response, apiResponse.getCode(), apiResponse.getMsg(), VersionConstants.V1);
            return body;
        } else if (body instanceof ResponseEntity responseEntity) {
            if (responseEntity.getBody() instanceof ApiResponse apiResponse) {
                attachHeader(response, apiResponse.getCode(), apiResponse.getMsg(), VersionConstants.V1);
                return body;
            }
        }

        attachHeader(response, ErrorCode.SUCCESS.getErrorCode(), ErrorCode.SUCCESS.getErrorMsg(), VersionConstants.V2);
        return body;
    }

    private void attachHeader(ServerHttpResponse response, int errorCode, String errorMsg, String version) {
        response.getHeaders().add(HeaderConstants.ERROR_CODE_HEADER, String.valueOf(errorCode));
        response.getHeaders().add(HeaderConstants.ERROR_MSG_HEADER, errorMsg == null ? "" : UrlCoder.encode(errorMsg));
        response.getHeaders().add(HeaderConstants.API_VERSION_HEADER, version);
    }
}
