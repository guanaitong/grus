/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by August.Zhou on 2023/12/1 14:15
 */
class Util {

    static String getFileContent(final String filePath) {
        String file = filePath;
        if (!file.startsWith("/")) {
            file = "/" + file;
        }
        try (InputStream in = Util.class.getResourceAsStream(file)) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (RuntimeException | IOException e) {
            throw new EsDataException(e);
        }
    }
}
