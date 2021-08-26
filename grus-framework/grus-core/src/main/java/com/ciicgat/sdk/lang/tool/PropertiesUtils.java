/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

/**
 * Created by August.Zhou on 2016/10/20 17:46.
 */
public class PropertiesUtils {
    public static final String SUFFIX = "properties";
    protected static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

    public static final Properties readFromText(String text) {
        Properties p = new Properties();
        try (Reader reader = new StringReader(text)) {
            p.load(reader);
        } catch (IOException e) {
            LOGGER.error(text, e);
        }
        return p;
    }

}
