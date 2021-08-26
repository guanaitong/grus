/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 读取配置文件工具类
 *
 * @author jonathan
 * @version $Id: ReadResourceUtils.java, v 0.1 2011-11-25 下午01:10:50 trunks Exp $
 */
public final class ReadResourceUtils {

    private static final Logger log = LoggerFactory.getLogger(ReadResourceUtils.class);
    private static Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

    private ReadResourceUtils() {
    }

    /**
     * 获取属性文件。<br />
     *
     * @param filename 文件的路径，方法内部会修改路径以"/"开始。
     * @return
     */
    public static synchronized Properties getPropertyFile(String filename) {
        Properties prop = null;
        if (propertiesMap.containsKey(filename)) {
            prop = propertiesMap.get(filename);
        } else {
            if (!filename.startsWith("/")) {
                filename = "/" + filename;
            }
            InputStream in = ReadResourceUtils.class.getResourceAsStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,
                    Charset.forName("UTF-8")));
            prop = new Properties();
            try {
                prop.load(reader);
            } catch (Exception e) {
                log.error("读取配置文件[" + filename + "]出错：", e);
            } finally {
                CloseUtils.close(reader);
                CloseUtils.close(in);
            }
            propertiesMap.put(filename, prop);
        }
        return prop;
    }

}

