/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
