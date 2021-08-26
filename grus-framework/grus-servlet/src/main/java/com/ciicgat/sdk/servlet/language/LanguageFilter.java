/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet.language;

import com.ciicgat.grus.language.LanguageConstant;
import com.ciicgat.grus.language.LanguageThreadLocal;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/03/17 10:33
 * @Description:
 */
public class LanguageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        String langTag = request.getHeader(LanguageConstant.LANG_HEADER);
        if (StringUtils.isNotEmpty(langTag)) {
            Locale locale = Locale.forLanguageTag(langTag);
            LanguageThreadLocal.setLocale(locale);
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            LanguageThreadLocal.remove();
        }
    }
}
