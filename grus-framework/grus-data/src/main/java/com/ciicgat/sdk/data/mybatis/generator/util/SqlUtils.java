/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.util;

import com.ciicgat.sdk.data.mybatis.generator.condition.SqlKeyword;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class SqlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlUtils.class);
    private static final Pattern SQL_PARAM_PATTERN = Pattern.compile("(@\\{)([\\w]+)(\\})");
    private static final Pattern TPL_PARAM_PATTERN = Pattern.compile("(@sql\\{)([\\w]+)(\\})");
    private static final String SQL_SEGMENT_PATH = "/code-generator/sql-segment.xml";
    private static final Map<String, String> SQL_SEGMENT = new ConcurrentHashMap<>();
    private static final String INJECTION_REGEX = "[A-Za-z0-9\\_\\-\\+\\.]+";

    private SqlUtils() {
    }

    /**
     * 检查字段名是否合法
     *
     * @param columnName 字段名
     */
    public static void checkColumnName(String columnName) {
        Assert.isTrue(!SqlUtils.isSQLInjection(columnName), "columnName is illegal: " + columnName);
    }

    public static boolean isSQLInjection(String str) {
        return !Pattern.matches(INJECTION_REGEX, str);
    }

    /**
     * get sql segment
     *
     * @return
     */
    public static Map<String, String> getSqlSegmentMap() {
        synchronized (SQL_SEGMENT) {
            if (SQL_SEGMENT.isEmpty()) {
                SQL_SEGMENT.putAll(doResolveSqlSegment());
            }
        }
        return SQL_SEGMENT;
    }

    private static Map<String, String> doResolveSqlSegment() {
        Map<String, String> map = new HashMap<>();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); // NOSONAR
            DocumentBuilder builder = builderFactory.newDocumentBuilder(); // NOSONAR
            InputStream inputStream = SqlUtils.class.getResourceAsStream(SQL_SEGMENT_PATH);
            Document document = builder.parse(inputStream);
            Element rootElement = document.getDocumentElement();
            NodeList childNodes = rootElement.getElementsByTagName("sql");
            for (int i = 0; i < childNodes.getLength(); i++) {
                Element element = (Element) childNodes.item(i);
                String id = element.getAttribute("id");
                String textContent = element.getTextContent();
                if (textContent.contains("@sql")) {
                    textContent = parseTplVariable(textContent, map);
                }
                map.put(id, textContent);
            }
        } catch (Exception e) {
            LOGGER.error("resolveSqlSegment", e);
        }
        return map;
    }

    public static String parseSqlVariable(String template, Map<String, String> paramMap) {
        return parseVariable(SQL_PARAM_PATTERN, template, paramMap);
    }

    public static String parseTplVariable(String template, Map<String, String> paramMap) {
        List<String> matches = getMatches(TPL_PARAM_PATTERN, template);
        if (matches.isEmpty()) {
            return template;
        }
        Map<String, String> newParamMap = new HashMap<>();
        matches.forEach(x -> newParamMap.put(x, replaceDollar(paramMap.get(x))));
        return restoreDollar(parseVariable(TPL_PARAM_PATTERN, template, newParamMap));
    }

    private static String replaceDollar(String content) {
        return content.replace("$", "&#36;");
    }

    private static String restoreDollar(String content) {
        return content.replace("&#36;", "$");
    }

    /**
     * 解析变量
     *
     * @param pattern  模式
     * @param template 模板 变量格式为 @{name}
     * @param paramMap 参数
     * @return
     */
    private static String parseVariable(Pattern pattern, String template, Map<String, String> paramMap) {
        Objects.requireNonNull(template);
        Objects.requireNonNull(paramMap);
        Matcher m = pattern.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String group = m.group(2);
            String value = paramMap.getOrDefault(group, "");
            m.appendReplacement(sb, value);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static List<String> getMatches(Pattern pattern, String template) {
        List<String> list = new ArrayList<>();
        Matcher m = pattern.matcher(template);
        while (m.find()) {
            list.add(m.group(2));
        }
        return list;
    }

    public static String wrapperScript(String sqlScript) {
        return String.format("<script>%s</script>", sqlScript);
    }

    public static String resolveMapperScript(String template, Map<String, String> paramMap) {
        return wrapperScript(parseSqlVariable(template, paramMap));
    }

    public static String firstToLowerCase(String param) {
        if (StringUtils.isBlank(param)) {
            return StringUtils.EMPTY;
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }

    public static String firstToUpperCase(String param) {
        if (StringUtils.isBlank(param)) {
            return StringUtils.EMPTY;
        }
        return param.substring(0, 1).toUpperCase() + param.substring(1);
    }

    public static String contactCondition(String columnName, SqlKeyword sqlKeyword) {
        Assert.notNull(columnName, "columnName is required");
        Assert.notNull(sqlKeyword, "sqlKeyword is required");
        SqlUtils.checkColumnName(columnName);
        return String.format("`%s` %s", columnName, sqlKeyword.getKeyword());
    }
}
