/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by August.Zhou on 2018/7/20 10:11.
 */
public class Systems {
    private static final Logger LOGGER = LoggerFactory.getLogger(Systems.class);
    public static final String UNKNOWN = "unknown";


    /**
     * 当前应用是否运行在k8s环境里
     */
    public static final boolean IN_K8S = !isEmpty(System.getenv("KUBERNETES_SERVICE_HOST"));


    public static final String APP_NAME;

    public static final String APP_SECRET;

    public static final String APP_INSTANCE;

    public static final String HOST_NAME;

    public static final String HOST_IP;

    public static final String WORK_ENV = System.getenv("WORK_ENV") == null ? "dev" : System.getenv("WORK_ENV");

    public static final String WORK_IDC = System.getenv("WORK_IDC") == null ? "ofc" : System.getenv("WORK_IDC");

    public static final String CLIENT_ID;


    static {
        String appInstanceKey;
        if (IN_K8S) {
            //在k8s里，使用HOSTNAME，VM里使用APP_INSTANCE_NAME
            appInstanceKey = "HOSTNAME";
        } else {
            appInstanceKey = "APP_INSTANCE_NAME";
        }

        APP_NAME = EnvPrepare.get("APP_NAME", () -> isEmpty(System.getenv("APP_NAME")) ? UNKNOWN : System.getenv("APP_NAME"));

        APP_SECRET = EnvPrepare.get("APP_SECRET", () -> isEmpty(System.getenv("APP_SECRET")) ? UNKNOWN : System.getenv("APP_SECRET"));


        APP_INSTANCE = EnvPrepare.get("APP_INSTANCE", () -> isEmpty(System.getenv(appInstanceKey)) ? UNKNOWN : System.getenv(appInstanceKey));

        String hostName;

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
        } catch (UnknownHostException e) {
            LOGGER.warn("warn", e);
            hostName = System.getenv("HOSTNAME");
        }
        final String hostNameCopy = hostName;

        HOST_NAME = EnvPrepare.get("HOST_NAME", () -> hostNameCopy);

        HOST_IP = EnvPrepare.get("HOST_IP", () -> getIp());

        CLIENT_ID = Systems.APP_INSTANCE + "-->" + Systems.HOST_IP + "-->" + UUID.randomUUID().toString();

        LOGGER.info("appName {},appInstance {},hostName {},hostIp {},clientId {}", APP_NAME, APP_INSTANCE, HOST_NAME, HOST_IP, CLIENT_ID);
    }

    /**
     * 获取本机IP地址.
     *
     * <p>
     * 有限获取外网IP地址.
     * 也有可能是链接着路由器的最终IP地址.
     * </p>
     *
     * @return 本机IP地址
     */
    private static String getIp() {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException ex) {
            LOGGER.warn("warn", ex);
            return UNKNOWN;
        }
        String localIpAddress = null;
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
            while (ipAddresses.hasMoreElements()) {
                InetAddress ipAddress = ipAddresses.nextElement();
                if (isPublicIpAddress(ipAddress)) {
                    String publicIpAddress = ipAddress.getHostAddress();
                    return publicIpAddress;
                }
                if (isLocalIpAddress(ipAddress)) {
                    localIpAddress = ipAddress.getHostAddress();
                }
            }
        }
        return localIpAddress == null ? UNKNOWN : localIpAddress;
    }

    private static boolean isPublicIpAddress(final InetAddress ipAddress) {
        return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isLocalIpAddress(final InetAddress ipAddress) {
        return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isV6IpAddress(final InetAddress ipAddress) {
        return ipAddress.getHostAddress().contains(":");
    }

    private static boolean isEmpty(String v) {
        return v == null || v.isBlank();
    }

}
