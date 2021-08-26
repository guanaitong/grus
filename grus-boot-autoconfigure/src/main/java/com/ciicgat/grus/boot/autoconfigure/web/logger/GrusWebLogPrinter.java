/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.logger.LogExclude;
import com.ciicgat.grus.logger.LogUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wei Jiaju
 * @author Stanley Shen
 */
@Aspect
public class GrusWebLogPrinter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusWebLogPrinter.class);

    private static final List<Class<?>> EXCLUDE_PARAM_TYPES = Arrays.asList(ServletRequest.class, ServletResponse.class, MultipartFile.class);

    private Pair<Boolean, LogExclude> checkIsPrintLog(Signature signature, Method targetMethod) {
        if ("isLive".equals(targetMethod.getName())) {
            return new ImmutablePair<>(false, null);
        }

        var clazz = signature.getDeclaringType();
        Annotation clazzRespBody = AnnotationUtils.getAnnotation(clazz, ResponseBody.class);
        Annotation methodRespBody = targetMethod.getAnnotation(ResponseBody.class);
        if (clazzRespBody == null && methodRespBody == null) {
            return new ImmutablePair<>(false, null);
        }

        LogExclude clazzLogExclude = AnnotationUtils.getAnnotation(clazz, LogExclude.class);
        LogExclude logExclude = targetMethod.getAnnotation(LogExclude.class);
        if (clazzLogExclude == null && logExclude == null) {
            return new ImmutablePair<>(true, null);
        }
        LogExclude targetLogExclude = logExclude == null ? clazzLogExclude : logExclude;
        if (targetLogExclude.excludeReq() && targetLogExclude.excludeResp()) {
            return new ImmutablePair<>(false, null);
        } else {
            return new ImmutablePair<>(true, targetLogExclude);
        }
    }


    @Around("execution(* *(..)) && (" +
            "within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Controller *) " +
            "))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        Method targetMethod = ((MethodSignature) signature).getMethod();

        Object[] params = joinPoint.getArgs();
        Pair<Boolean, LogExclude> logExcludePair = checkIsPrintLog(signature, targetMethod);
        if (!logExcludePair.getLeft()) {
            // 不需要打印日志
            return joinPoint.proceed(params);
        }
        // 需要打印日志
        LogExclude logExclude = logExcludePair.getRight();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + targetMethod.getName();

        if (LogUtil.checkPrintReq(logExclude)) {
            List<Object> paramsList = Arrays.asList(params);
            paramsList = paramsList.stream().filter(obj -> {
                        for (var excludeType : EXCLUDE_PARAM_TYPES) {
                            if (excludeType.isInstance(obj)) {
                                return false;
                            }
                        }
                        return true;
                    }
            ).collect(Collectors.toList());
            LOGGER.info("WEB_REQ METHOD: {} PARAM: {}", methodName, LogUtil.truncate(JSON.toJSONString(paramsList)));
        }
        try {
            Object resp = joinPoint.proceed(params);
            if (LogUtil.checkPrintResp(logExclude)) {
                LOGGER.info("WEB_RSP METHOD: {} RESULT: {}", methodName, LogUtil.truncate(JSON.toJSONString(resp)));
            }
            return resp;
        } catch (Throwable e) {
            LOGGER.error("WEB_EX METHOD: {} ERROR:", methodName, e);
            throw e;
        }
    }

}
