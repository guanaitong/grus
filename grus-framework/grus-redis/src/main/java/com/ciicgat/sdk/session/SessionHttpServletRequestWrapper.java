/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Created by August.Zhou on 2018/1/8 16:17.
 */
@SuppressWarnings("deprecation")
public class SessionHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletResponse httpServletResponse;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public SessionHttpServletRequestWrapper(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        super(request);
        this.httpServletResponse = httpServletResponse;
    }

    private HttpServletRequest __getHttpServletRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return SessionManager.getCurrentSession(__getHttpServletRequest(), httpServletResponse);
    }

    @Override
    public HttpSession getSession() {
        return SessionManager.getCurrentSession(__getHttpServletRequest(), httpServletResponse);
    }

    @Override
    public String getRequestedSessionId() {
        return SessionManager.getCurrentSession(__getHttpServletRequest(), httpServletResponse).getId();
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }
}
