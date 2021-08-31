/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 读取配置文件工具类
 *
 * @author jonathan
 * @version $Id: ReadResourceUtils.java, v 0.1 2011-11-25 下午01:10:50 trunks Exp $
 */
public final class ReadResourceUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadResourceUtils.class);
    private static ConcurrentMap<String, Properties> propertiesMap = new ConcurrentHashMap<>();

    private ReadResourceUtils() {
    }

    /**
     * 获取属性文件。<br />
     *
     * @param filename 文件的路径，方法内部会修改路径以"/"开始。
     * @return
     */
    public static Properties getPropertyFile(String filename) {
        Properties prop = propertiesMap.get(filename);
        if (prop != null) {
            return prop;
        }
        return propertiesMap.computeIfAbsent(filename, filename1 -> {
            if (!filename1.startsWith("/")) {
                filename1 = "/" + filename1;
            }
            InputStream in = ReadResourceUtils.class.getResourceAsStream(filename1);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Properties prop1 = new Properties();
            try {
                prop1.load(reader);
            } catch (Exception e) {
                LOGGER.error("读取配置文件[" + filename1 + "]出错：", e);
            } finally {
                CloseUtils.close(reader);
                CloseUtils.close(in);
            }
            return prop1;
        });
    }

}

