/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeConfig;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.tool.RollingNumber;
import com.ciicgat.sdk.servlet.fallback.FallbackHelper;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-06-19 13:51.
 */
public class GrusFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusFilter.class);

    private GrusRuntimeConfig grusRuntimeConfig = GrusFramework.getGrusRuntimeManager().getGrusRuntimeConfig();

    private final AtomicInteger current = new AtomicInteger();

    private final RollingNumber rollingNumber = new RollingNumber();


    private ConfigCollection configCollection;

    public GrusFilter() {
        try {
            configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection();
        } catch (Exception e) {
            configCollection = null;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        J2EEContainer.init(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        int con = current.incrementAndGet(); //并发数
        int qps = rollingNumber.record(); //qps
        if (con > 128) {
            String logMsg = "服务并发有点高,并发数:" + con + ",qps:" + qps;
            LOGGER.warn(logMsg);
            FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.SERVLET, logMsg, null);
            FrigateNotifier.sendMessageByAppName(logMsg);
        } else if (con > 64 && con > 4 * qps) {
            String logMsg = "服务性能有问题,并发数远远超过qps,并发数:" + con + ",qps:" + qps;
            LOGGER.warn(logMsg);
            FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.SERVLET, logMsg, null);
        }

        // 服务端降级
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest request = (HttpServletRequest) servletRequest;
            final HttpServletResponse response = (HttpServletResponse) servletResponse;
            if (FallbackHelper.isUriNeedFallback(request.getRequestURI(), configCollection)) {
                response.setContentType("application/json; charset=utf-8");
                response.getWriter().append(JSON.toJSONString(ErrorCode.REQUEST_BLOCK));
                return;
            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            current.decrementAndGet();
        }
    }
}
