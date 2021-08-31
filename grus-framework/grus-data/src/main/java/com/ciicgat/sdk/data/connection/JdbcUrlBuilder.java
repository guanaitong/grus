/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.connection;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-06-28 14:56.
 */
public class JdbcUrlBuilder {
    private static final String JDBC_TPL = "jdbc:mysql://%s:%s";

    private static final Map<String, String> DEFAULT_PARAMS;

    static {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("zeroDateTimeBehavior", "convertToNull");
        map.put("characterEncoding", "UTF8");
        map.put("autoReconnect", "true");
//        map.put("useAffectedRows", "true");
        map.put("failOverReadOnly", "false");
        // mysql高版本驱动需要设置时区：
        // https://www.cnblogs.com/liaojie970/p/8916568.html
        // https://www.jianshu.com/p/0d53218da27d
        map.put("serverTimezone", "GMT%2B8");
        map.put("useSSL", "false");
        //MYSQL8.0以上(以允许客户端从服务器获取公钥)
        map.put("allowPublicKeyRetrieval", "true");
        DEFAULT_PARAMS = Collections.unmodifiableMap(map);
    }


    public static String build(String host, String port) {
        return build(host, port, null, null);
    }

    public static String build(String host, String port, final String dbName) {
        return build(host, port, dbName, null);
    }


    public static String build(String host, String port, final String dbName, Map<String, String> extParams) {
        StringBuilder sb = new StringBuilder(String.format(JDBC_TPL, host, port));
        if (StringUtils.isNoneBlank(dbName)) {
            sb.append('/').append(dbName);
        }
        Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        //保证外部的参数会覆盖默认参数
        if (extParams != null) {
            params.putAll(extParams);
        }
        sb.append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


}
