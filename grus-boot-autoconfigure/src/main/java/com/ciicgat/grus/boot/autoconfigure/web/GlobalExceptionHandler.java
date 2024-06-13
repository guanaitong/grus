/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import com.ciicgat.api.core.BusinessFeignException;
import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.boot.autoconfigure.core.CoreProperties;
import com.ciicgat.grus.util.ClassType;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.convert.StandardErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import feign.FeignException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 全局默认的异常处理，默认最低优先级，如果当前应用没有设置异常处理，那么会走到这里。
 * code规则详细见：https://guide.wuxingdev.cn/microservice/code.html
 * Created by August.Zhou on 2019-05-29 10:36.
 */
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MeterRegistry registry;

    private final WebProperties webProperties;

    private final CoreProperties coreProperties;

    public GlobalExceptionHandler(MeterRegistry registry, WebProperties webProperties, CoreProperties coreProperties) {
        this.registry = registry;
        this.webProperties = webProperties;
        this.coreProperties = coreProperties;
    }

    /**
     * 一般是当前系统业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessRuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessRuntimeException(BusinessRuntimeException e) {
        if (webProperties.isPrintBusinessErrorStack()) {
            LOGGER.warn(e.toString(), e);
        } else {
            LOGGER.warn("BusinessRuntimeException, errorCode {} errorMsg {}", e.getErrorCode(), e.getErrorMsg());
        }
        metricException(true, e);
        return ResponseEntity.status(e.getHttpStatusCode()).body(e.fail());
    }

    /**
     * 一般是依赖服务业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessFeignException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessFeignException(BusinessFeignException e) {
        if (webProperties.isPrintBusinessErrorStack()) {
            LOGGER.warn(e.toString(), e);
        } else {
            LOGGER.warn("BusinessFeignException, errorCode {} errorMsg {}", e.getErrorCode(), e.getErrorMsg());
        }
        metricException(true, e);
        return of(HttpStatus.OK, e);
    }

    /**
     * 依赖系统调用异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Object>> handleFeignException(FeignException e) {
        LOGGER.error("error", e);
        metricException(false, e);
        return of(HttpStatus.INTERNAL_SERVER_ERROR, new StandardErrorCode(webProperties.getErrorCodePrefix(), 4, 0, "依赖服务异常"));
    }

    /**
     * 一般ServletException都是http请求方式不对引起的异常，可以认为是参数错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({ServletException.class, MissingServletRequestParameterException.class, HttpRequestMethodNotSupportedException.class, NoHandlerFoundException.class})
    public ResponseEntity<ApiResponse<Object>> handleServletException(ServletException e) {
        LOGGER.warn("error", e);
        metricException(false, e);
        String errorMsg = "请求格式不正确";
        if (e.getClass() == MissingServletRequestParameterException.class) {
            errorMsg = "请求参数缺失";
        }
        HttpStatus status = e instanceof HttpRequestMethodNotSupportedException ? HttpStatus.METHOD_NOT_ALLOWED : HttpStatus.BAD_REQUEST;
        return of(status, new StandardErrorCode(webProperties.getErrorCodePrefix(), 2, 0, errorMsg));
    }


    /**
     * 一般的，TypeMismatchException也可以认为是参数错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({TypeMismatchException.class, MethodArgumentTypeMismatchException.class, MethodArgumentConversionNotSupportedException.class})
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(TypeMismatchException e) {
        LOGGER.warn("TypeMismatchException, error: " + e.getMessage());
        metricException(false, e);
        return of(HttpStatus.BAD_REQUEST, new StandardErrorCode(webProperties.getErrorCodePrefix(), 2, 0, "前端参数错误"));
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> handleBindException(BindException e) {
        LOGGER.warn("BindException, param validate error: " + e.getMessage());
        metricException(false, e);
        return handleBindingResult(e.getBindingResult());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.warn("MethodArgumentNotValidException, param validate error: " + e.getMessage());
        metricException(false, e);
        return handleBindingResult(e.getBindingResult());
    }

    private ApiResponse<Object> handleBindingResult(BindingResult bindingResult) {
        String errorMsg = "前端参数错误";
        if (bindingResult.hasErrors()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError && webProperties.isShowFieldNameInError()) {
                    var fieldError = (FieldError) error;
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
        metricException(false, e);
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

    //      GlobalExceptionHandler会在有web、service下运行，在web下运行时，依赖的jar包里是没有spring-jdbc、spring-tx包依赖的。
    //      所以没办法直接像handleBusinessFeignException方法那样子处理异常，只能用classname来判断。
    private static ClassType dataAccessExceptionClassType = new ClassType("org.springframework.dao.DataAccessException");
    private static ClassType rabbitMqExceptionClassType = new ClassType("com.ciicgat.grus.rabbitmq.RabbitMqException");
    private static ClassType esDataExceptionClassType = new ClassType("com.ciicgat.grus.elasticsearch.core.EsDataException");
    private static ClassType jedisExceptionClassType = new ClassType("redis.clients.jedis.exceptions.JedisException");

    private static ClassType redisSystemExceptionClassType = new ClassType("org.springframework.data.redis.RedisSystemException");
    private static ClassType blockExceptionClassType = new ClassType("com.alibaba.csp.sentinel.slots.block.BlockException");
    private static ClassType clientAbortExceptionClassType = new ClassType("org.apache.catalina.connector.ClientAbortException");

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> handleThrowable(HttpServletRequest request, Throwable throwable) {
        //为了应用迁移方便
        if (throwable instanceof ErrorCode) {
            var errorCode = (ErrorCode) throwable;
            LOGGER.warn("{}, errorCode {} errorMsg {}", throwable.getClass().getSimpleName(), ((ErrorCode) throwable).getErrorCode(), ((ErrorCode) throwable).getErrorMsg());
            return of(HttpStatus.OK, errorCode);
        }


        StringBuffer requestInfo = request.getRequestURL();
        requestInfo.append("，params=>");
        request.getParameterMap().forEach((key, value) -> requestInfo.append(key).append(':').append(Arrays.toString(value)));
        String msg = "发生异常，请求信息为：" + requestInfo;
        LOGGER.error(msg, throwable);

        metricException(false, throwable);

        if (!clientAbortExceptionClassType.isInstance(throwable)) {
            Alert.send(msg, throwable);
        }

        if (blockExceptionClassType.isInstance(throwable)) {
            return of(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.REQUEST_BLOCK);
        }

        String errorMsg = "系统异常";
        if (jedisExceptionClassType.isInstance(throwable) || redisSystemExceptionClassType.isInstance(throwable)) {
            errorMsg = "redis访问异常";
        } else if (rabbitMqExceptionClassType.isInstance(throwable)) {
            errorMsg = "消息队列访问异常";
        } else if (esDataExceptionClassType.isInstance(throwable)) {
            errorMsg = "elasticsearch访问异常";
        } else if (dataAccessExceptionClassType.isInstance(throwable)) {
            Throwable throwableCause = throwable.getCause();
            if (throwableCause instanceof SQLException) {
                errorMsg = "数据库访问异常";
            }
        }
        LOGGER.error(errorMsg);
        return of(HttpStatus.INTERNAL_SERVER_ERROR, new StandardErrorCode(webProperties.getErrorCodePrefix(), 1, 0, errorMsg));
    }

    private ResponseEntity<ApiResponse<Object>> of(HttpStatus status, ErrorCode errorCode) {
        if (errorCode.getErrorCode() == ErrorCode.REQUEST_BLOCK.getErrorCode()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ApiResponse.fail(errorCode));
        }
        return ResponseEntity.status(status).body(ApiResponse.fail(errorCode));
    }

    private void metricException(boolean isBusinessException, Throwable throwable) {
        if (isBusinessException && !coreProperties.isMetricBusinessException())
            return;
        if (!isBusinessException && !coreProperties.isMetricSystemException())
            return;
        List<Tag> tags = new ArrayList();
//        tags.add(Tag.of("app_name", Systems.APP_NAME));
//        tags.add(Tag.of("app_instance", Systems.APP_INSTANCE));
//        tags.add(Tag.of("host_name", Systems.HOST_NAME));
        if (throwable instanceof ErrorCode) {
            tags.add(Tag.of("error_code", "" + ((ErrorCode) throwable).getErrorCode()));
        }
        tags.add(Tag.of("error_type", throwable.getClass().toString()));
        if (throwable.getMessage() != null) {
            tags.add(Tag.of("error_msg", throwable.getMessage()));
        }
        this.registry.counter(isBusinessException ? "grus_business_exception" : "grus_system_exception", tags).increment();
    }

}
