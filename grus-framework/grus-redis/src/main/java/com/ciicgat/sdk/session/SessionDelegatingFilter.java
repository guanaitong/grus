/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Created by August.Zhou on 2018/1/8 16:07.
 */
public class SessionDelegatingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest
                && response instanceof HttpServletResponse) {
            chain.doFilter(new SessionHttpServletRequestWrapper((HttpServletRequest) request, (HttpServletResponse) response), response);
            return;
        }

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
