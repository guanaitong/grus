/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.tool.PropertiesUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by August.Zhou on 2021/9/1 14:46.
 */
class ValueWrappers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueWrappers.class);

    /**
     * 目前支持的类型为json、properties，以后可以添加yml、xml的支持
     *
     * @param key
     * @param value
     * @return
     */
    static ValueWrapper of(String key, String value) {
        try {
            JSON.parse(value);
            LinkedHashMap<String, Object> p = JSON.parse(value, new TypeReference<>() {
            });
            if (!key.endsWith(".json")) {  //提醒下那些文件名命名不规范的用户
                LOGGER.warn("you file is json format,but name is" + key, "please change it with .json suffix");
            }
            return new JsonValueWrapper(value, p);
        } catch (Exception e) {
            if (!isProperties(value)) {
                return new DefaultValueWrapper(value);
            }
            Properties p = new Properties();
            try (Reader reader = new StringReader(value)) {
                p.load(reader);
            } catch (Exception e1) {
            }
            if (p.size() > 0) {
                if (!key.endsWith(PropertiesUtils.SUFFIX)) {  //提醒下那些文件名命名不规范的用户
                    LOGGER.warn("you file is properties format,but name is" + key, "please change it with .properties suffix");
                }
                return new PropertiesValueWrapper(value, p);
            }
        }
        return new DefaultValueWrapper(value);
    }

    // Properties.load方法，会把所有的字符串转化为Properties对象，基本不会报错。我们根据properties规则判断下。
    private static boolean isProperties(String content) {
        try (Reader reader = new StringReader(content)) {
            List<String> lines = IOUtils.readLines(reader);
            for (String line : lines) {
                if (line != null && !line.startsWith("#") && line.contains("=")) {
                    return true;
                }
            }
        } catch (IOException e) {
        }
        return false;
    }
}
