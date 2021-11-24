/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import com.ciicgat.api.core.BusinessFeignException;
import com.ciicgat.grus.alert.Alert;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.convert.StandardErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

/**
 * 全局默认的异常处理，默认最低优先级，如果当前应用没有设置异常处理，那么会走到这里。
 * code规则详细见：https://guide.wuxingdev.cn/microservice/code.html
 * Created by August.Zhou on 2019-05-29 10:36.
 */
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private WebProperties webProperties;

    public GlobalExceptionHandler(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    /**
     * 一般是依赖服务业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessFeignException.class)
    public ApiResponse<Object> handleBusinessFeignException(BusinessFeignException e) {
        if (webProperties.isPrintBusinessErrorStack()) {
            LOGGER.warn(e.toString(), e);
        } else {
            LOGGER.warn("BusinessFeignException, errorCode {} errorMsg {}", e.getErrorCode(), e.getErrorMsg());
        }
        return ApiResponse.fail(e);
    }

    /**
     * 依赖系统调用异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(FeignException.class)
    public ApiResponse<Object> handleFeignException(FeignException e) {
        LOGGER.error("error", e);
        return ApiResponse.fail(new StandardErrorCode(webProperties.getErrorCodePrefix(), 4, 0, "依赖服务异常"));
    }


    /**
     * 一般是当前系统业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessRuntimeException.class)
    public ApiResponse<Object> handleBusinessRuntimeException(BusinessRuntimeException e) {
        if (webProperties.isPrintBusinessErrorStack()) {
            LOGGER.warn(e.toString(), e);
        } else {
            LOGGER.warn("BusinessRuntimeException, errorCode {} errorMsg {}", e.getErrorCode(), e.getErrorMsg());
        }
        return ApiResponse.fail(e);
    }


    /**
     * 一般ServletException都是http请求方式不对引起的异常，可以认为是参数错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            ServletException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            NoHandlerFoundException.class})
    public ApiResponse<Object> handleServletException(ServletException e) {
        LOGGER.warn("error", e);
        String errorMsg = "请求格式不正确";
        if (e.getClass() == MissingServletRequestParameterException.class) {
            errorMsg = "请求参数缺失";
        }

        return ApiResponse.fail(new StandardErrorCode(webProperties.getErrorCodePrefix(), 2, 0, errorMsg));
    }


    /**
     * 一般的，TypeMismatchException也可以认为是参数错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            TypeMismatchException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentConversionNotSupportedException.class})
    public ApiResponse<Object> handleTypeMismatchException(TypeMismatchException e) {
        LOGGER.warn("TypeMismatchException, error: " + e.getMessage());
        return ApiResponse.fail(new StandardErrorCode(webProperties.getErrorCodePrefix(), 2, 0, "前端参数错误"));
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> handleBindException(BindException e) {
        LOGGER.warn("BindException, param validate error: " + e.getMessage());
        return handleBindingResult(e.getBindingResult());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.warn("MethodArgumentNotValidException, param validate error: " + e.getMessage());
        return handleBindingResult(e.getBindingResult());
    }

    private ApiResponse<Object> handleBindingResult(BindingResult bindingResult) {
        String errorMsg = "前端参数错误";
        if (bindingResult.hasErrors()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError fieldError && webProperties.isShowFieldNameInError()) {
                    stringBuilder.append((fieldError).getField()).append(' ').append(fieldError.getDefaultMessage()).append(' ');
                } else {
                    stringBuilder.append(error.getDefaultMessage()).append(' ');
                }
            }
            if (stringBuilder.length() > 1) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            errorMsg = stringBuilder.toString();
        }
        return ApiResponse.fail(new StandardErrorCode(webProperties.getErrorCodePrefix(), 2, 0, errorMsg));
    }


    /**
     * 一般的，ConstraintViolationException都是参数错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Object> handleValidationException(ConstraintViolationException e) {
        LOGGER.warn("ConstraintViolationException param validate error: " + e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<?> item : violations) {
            stringBuilder.append(item.getMessage()).append(' ');
        }
        if (stringBuilder.length() > 1) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return ApiResponse.fail(new StandardErrorCode(webProperties.getErrorCodePrefix(), 2, 0, stringBuilder.toString()));
    }


    @ExceptionHandler
    public ApiResponse<Object> handleThrowable(HttpServletRequest request, Throwable throwable) {
        //为了应用迁移方便
        if (throwable instanceof ErrorCode errorCode) {
            LOGGER.warn("{}, errorCode {} errorMsg {}", throwable.getClass().getSimpleName(), ((ErrorCode) throwable).getErrorCode(), ((ErrorCode) throwable).getErrorMsg());
            return ApiResponse.fail(errorCode);
        }

        StringBuffer requestInfo = request.getRequestURL();
        requestInfo.append("，params=>");
        request.getParameterMap().forEach((key, value) -> requestInfo.append(key).append(':').append(Arrays.toString(value)));
        String msg = "发生异常，请求信息为：" + requestInfo;
        Alert.send(msg, throwable);
        LOGGER.error(msg, throwable);
        String errorMsg = "系统异常";
        if (throwable instanceof SQLException) {
            errorMsg = "数据库异常";
        }
        return ApiResponse.fail(new StandardErrorCode(webProperties.getErrorCodePrefix(), 1, 0, errorMsg));
    }


}
