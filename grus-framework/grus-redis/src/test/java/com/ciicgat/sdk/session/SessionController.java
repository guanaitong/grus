/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by August.Zhou on 2018-11-15 13:15.
 */
@RestController
public class SessionController {

    @RequestMapping("/write")
    public String write(@RequestParam String r, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        HttpSession httpSession = SessionManager.getCurrentSession(httpServletRequest, httpServletResponse);
        HttpSession httpSession2 = SessionManager.getCurrentSession(httpServletRequest, httpServletResponse);
        if (httpSession != httpSession2) {
            throw new RuntimeException();
        }
        httpSession.setAttribute("key", r);
        System.out.println(httpSession.getId());
        return r;
    }

    @RequestMapping("/read")
    public String read(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String sessionId = SessionManager.getCookieValue(SessionManager.SESSION_ID_COOKIE_NAME, httpServletRequest);
        if (sessionId.isEmpty()) {
            throw new RuntimeException();
        }
        HttpSession httpSession = SessionManager.getCurrentSession(httpServletRequest, httpServletResponse);
        System.out.println(httpSession.getId());
        return (String) httpSession.getAttribute("key");
    }
}
