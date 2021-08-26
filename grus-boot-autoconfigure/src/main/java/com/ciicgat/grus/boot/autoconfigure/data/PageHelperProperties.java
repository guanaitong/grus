/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * 分页插件参数对象
 * https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md
 *
 * @author wanchongyang
 * @date 2019/03/01
 */
@ConfigurationProperties(prefix = PageHelperProperties.PAGEHELPER_PREFIX)
public class PageHelperProperties {
    public static final String PAGEHELPER_PREFIX = "grus.pagehelper";

    private Properties properties = new Properties();

    /**
     * 分页插件会自动检测当前的数据库链接，自动选择合适的分页方式。 你可以配置helperDialect属性来指定分页插件使用哪种方言。配置时，可以使用下面的缩写值：
     * oracle,mysql,mariadb,sqlite,hsqldb,postgresql,db2,sqlserver,informix,h2,sqlserver2012,derby
     */
    private String helperDialect = "mysql";

    /**
     * 默认值为 false，该参数对使用 RowBounds 作为分页参数时有效。 当该参数设置为 true 时，会将 RowBounds 中的 offset 参数当成 pageNum 使用，可以用页码和页面大小两个参数进行分页。
     */
    private String offsetAsPageNum;

    /**
     * 默认值为false，该参数对使用 RowBounds 作为分页参数时有效。 当该参数设置为true时，使用 RowBounds 分页会进行 count 查询。
     */
    private String rowBoundsWithCount;

    /**
     * 默认值为 false，当该参数设置为 true 时，如果 pageSize=0 或者 RowBounds.limit = 0 就会查询出全部的结果（相当于没有执行分页查询，但是返回结果仍然是 Page 类型）。
     */
    private String pageSizeZero;

    /**
     * 默认值为 true。当使用运行时动态数据源或没有设置 helperDialect 属性自动获取数据库类型时，会自动获取一个数据库连接， 通过该属性来设置是否关闭获取的这个连接，默认true关闭，设置为 false 后，不会关闭获取的连接，这个参数的设置要根据自己选择的数据源来决定。
     */
    private String closeConn;

    /**
     * 分页合理化参数，默认值为false。当该参数设置为 true 时，pageNum<=0 时会查询第一页， pageNum>pages（超过总数时），
     * 会查询最后一页。默认false 时，直接根据参数进行查询。
     */
    private String reasonable = "false";
    /**
     * 支持通过 Mapper 接口参数来传递分页参数，默认值true，分页插件会从查询方法的参数值中，自动根据上面 params 配置的字段中取值，查找到合适的值时就会自动分页。
     */
    private String supportMethodsArguments = "true";
    /**
     * 为了支持startPage(Object params)方法，增加了该参数来配置参数映射，用于从对象中根据属性名取值，
     * 可以配置 pageNum,pageSize,count,pageSizeZero,reasonable，不配置映射的用默认值，
     * 默认值为pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero。
     */
    private String params;
    /**
     * 默认值为 true。设置为 true 时，允许在运行时根据多数据源自动识别对应方言的分页
     */
    private String autoRuntimeDialect = "true";
    /**
     * 用于控制默认不带 count 查询的方法中，是否执行 count 查询，默认 true 会执行 count 查询，这是一个全局生效的参数，多数据源时也是统一的行为
     */
    private String defaultCount;
    /**
     * 允许配置自定义实现的别名，可以用于根据JDBCURL自动获取对应实现，允许通过此种方式覆盖已有的实现，配置示例如(多个配置用分号;隔开)：
     * grus.pagehelper.dialect-alias=oracle=com.github.pagehelper.dialect.helper.OracleDialect
     */
    private String dialectAlias;

    /**
     * 默认情况下会使用 PageHelper 方式进行分页，如果想要实现自己的分页逻辑，可以实现 Dialect(com.github.pagehelper.Dialect) 接口，然后配置该属性为实现类的全限定名称。
     */
    private String dialect;

    /**
     * 自动获取dialect
     */
    private String autoDialect;

    public PageHelperProperties() {
        properties.setProperty("helperDialect", helperDialect);
        properties.setProperty("reasonable", reasonable);
        properties.setProperty("supportMethodsArguments", supportMethodsArguments);
        properties.setProperty("autoRuntimeDialect", autoRuntimeDialect);
    }

    public Properties getProperties() {
        return properties;
    }

    public Boolean getOffsetAsPageNum() {
        return Boolean.valueOf(properties.getProperty("offsetAsPageNum"));
    }

    public void setOffsetAsPageNum(Boolean offsetAsPageNum) {
        properties.setProperty("offsetAsPageNum", offsetAsPageNum.toString());
    }

    public Boolean getRowBoundsWithCount() {
        return Boolean.valueOf(properties.getProperty("rowBoundsWithCount"));
    }

    public void setRowBoundsWithCount(Boolean rowBoundsWithCount) {
        properties.setProperty("rowBoundsWithCount", rowBoundsWithCount.toString());
    }

    public Boolean getPageSizeZero() {
        return Boolean.valueOf(properties.getProperty("pageSizeZero"));
    }

    public void setPageSizeZero(Boolean pageSizeZero) {
        properties.setProperty("pageSizeZero", pageSizeZero.toString());
    }

    public Boolean getReasonable() {
        return Boolean.valueOf(properties.getProperty("reasonable"));
    }

    public void setReasonable(Boolean reasonable) {
        properties.setProperty("reasonable", reasonable.toString());
    }

    public Boolean getSupportMethodsArguments() {
        return Boolean.valueOf(properties.getProperty("supportMethodsArguments"));
    }

    public void setSupportMethodsArguments(Boolean supportMethodsArguments) {
        properties.setProperty("supportMethodsArguments", supportMethodsArguments.toString());
    }

    public String getDialect() {
        return properties.getProperty("dialect");
    }

    public void setDialect(String dialect) {
        properties.setProperty("dialect", dialect);
    }

    public String getHelperDialect() {
        return properties.getProperty("helperDialect");
    }

    public void setHelperDialect(String helperDialect) {
        properties.setProperty("helperDialect", helperDialect);
    }

    public Boolean getAutoRuntimeDialect() {
        return Boolean.valueOf(properties.getProperty("autoRuntimeDialect"));
    }

    public void setAutoRuntimeDialect(Boolean autoRuntimeDialect) {
        properties.setProperty("autoRuntimeDialect", autoRuntimeDialect.toString());
    }

    public Boolean getAutoDialect() {
        return Boolean.valueOf(properties.getProperty("autoDialect"));
    }

    public void setAutoDialect(Boolean autoDialect) {
        properties.setProperty("autoDialect", autoDialect.toString());
    }

    public Boolean getCloseConn() {
        return Boolean.valueOf(properties.getProperty("closeConn"));
    }

    public void setCloseConn(Boolean closeConn) {
        properties.setProperty("closeConn", closeConn.toString());
    }

    public String getParams() {
        return properties.getProperty("params");
    }

    public void setParams(String params) {
        properties.setProperty("params", params);
    }

    public Boolean getDefaultCount() {
        return Boolean.valueOf(properties.getProperty("defaultCount"));
    }

    public void setDefaultCount(Boolean defaultCount) {
        properties.setProperty("defaultCount", defaultCount.toString());
    }

    public String getDialectAlias() {
        return properties.getProperty("dialectAlias");
    }

    public void setDialectAlias(String dialectAlias) {
        properties.setProperty("dialectAlias", dialectAlias);
    }
}
