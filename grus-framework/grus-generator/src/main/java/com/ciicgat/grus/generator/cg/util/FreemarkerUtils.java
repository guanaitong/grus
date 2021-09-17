/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * ftl解析工具
 * <p>
 * Created by Clive at 2020/11/07
 */
public class FreemarkerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerUtils.class);
    private static final String ENCODING = "UTF-8";

    /**
     * 解析模板
     *
     * @param filePath     模板文件路径 (resources下的路径)
     * @param dataModelMap 模板中的变量和值
     * @return 返回解析后的内容
     */
    public static String parseTemplate(String filePath, Map<String, Object> dataModelMap) {
        try {
            int index = filePath.lastIndexOf('/');
            String basePackagePath = filePath.substring(0, index + 1);
            String fileName = filePath.substring(index + 1);
            Template template = getTemplateByName(basePackagePath, fileName);
            StringWriter strWriter = new StringWriter();
            template.setOutputEncoding(ENCODING);
            template.process(dataModelMap, strWriter);
            return strWriter.toString();
        } catch (Exception e) {
            LOGGER.error("parseTemplate Exc", e);
            throw new IllegalArgumentException("freemarker 模板解析失败", e);
        }
    }

    @SuppressWarnings("deprecation")
    private static Template getTemplateByName(String basePackagePath, String fileName) throws IOException {
        Configuration configuration = new Configuration();
        configuration.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        configuration.setClassLoaderForTemplateLoading(FreemarkerUtils.class.getClassLoader(), basePackagePath);
        configuration.setEncoding(Locale.ENGLISH, ENCODING);
        configuration.setDefaultEncoding(ENCODING);
        configuration.setOutputEncoding(ENCODING);
        return configuration.getTemplate(fileName);
    }

}
