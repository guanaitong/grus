/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import javax.servlet.ServletContext;

/**
 * Java Servlet 容器类型
 * Created by August.Zhou on 2018/8/6 17:35.
 */
public enum J2EEContainer {
    UNKNOWN,
    JETTY,
    TOMCAT,
    WEBSPHERE,
    WEBLOGIC,
    JBOSS,
    RESIN;

    private static J2EEContainer PROVIDER = UNKNOWN;


    /**
     * 根据serverInfo判断当前处于哪个servlet容器
     *
     * @param servletContext
     */
    public static void init(ServletContext servletContext) {
        String serverInfo = servletContext.getServerInfo();
        if (serverInfo.startsWith("Jetty")) {
            PROVIDER = JETTY;
        } else if (serverInfo.startsWith("IBM WebSphere")) {
            PROVIDER = WEBSPHERE;
        } else if (serverInfo.startsWith("WebLogic")) {
            PROVIDER = WEBLOGIC;
        } else if (serverInfo.startsWith("JBoss")) {
            PROVIDER = JBOSS;
        } else if (serverInfo.startsWith("Apache Tomcat")) {
            PROVIDER = TOMCAT;
        } else if (serverInfo.startsWith("Resin")) {
            PROVIDER = RESIN;
        }
    }

    public static J2EEContainer getCurrentContainer() {
        return PROVIDER;
    }

}
