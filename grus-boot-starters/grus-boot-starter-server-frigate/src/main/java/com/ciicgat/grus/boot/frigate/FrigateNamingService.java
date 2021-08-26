/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.frigate;

import com.ciicgat.grus.service.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by August.Zhou on 2019-03-25 18:14.
 */
public class FrigateNamingService implements NamingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrigateNamingService.class);

    private Properties endPointProperties;


    public FrigateNamingService() {
        String RUN_ENV = System.getenv("WORK_ENV") == null ? "local" : System.getenv("WORK_ENV");
        String fileName = "endpoint/endpoint-" + RUN_ENV + ".properties";
        endPointProperties = getPropertyFile(fileName);
    }

    @Override
    public String resolve(String serviceName) {
        String host = System.getenv("KUBERNETES_SERVICE_HOST");
        if (host != null && !host.isBlank()) {
            return "http://" + serviceName;
        }
        return endPointProperties.getProperty(serviceName);
    }

    private synchronized Properties getPropertyFile(String filename) {

        if (!filename.startsWith("/")) {
            filename = "/" + filename;
        }
        Properties prop = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FrigateNamingService.class.getResourceAsStream(filename),
                Charset.forName("UTF-8")))) {
            prop.load(reader);
        } catch (Exception e) {
            LOGGER.error("读取配置文件[" + filename + "]出错：", e);
        }
        return prop;
    }
}
