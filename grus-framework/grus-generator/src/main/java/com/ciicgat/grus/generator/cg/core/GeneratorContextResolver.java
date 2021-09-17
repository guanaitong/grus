/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import com.ciicgat.grus.generator.cg.db.ColumnInfo;
import com.ciicgat.grus.generator.cg.db.ConnectionFactory;
import com.ciicgat.grus.generator.cg.db.JdbcType;
import com.ciicgat.grus.generator.cg.db.JdbcTypeResolver;
import com.ciicgat.grus.generator.cg.db.TableInfo;
import com.ciicgat.grus.generator.cg.db.TableIntrospect;
import com.ciicgat.grus.generator.cg.db.MysqlTableIntrospect;
import com.ciicgat.grus.generator.cg.util.GeneratorUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.lang.model.SourceVersion;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 配置上下文解析器
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class GeneratorContextResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorContextResolver.class);
    private static final String CONFIG_XML_NAME = "code-generator.xml";
    private static final String DEFAULT_CONFIG_FILE_PATH = "/" + CONFIG_XML_NAME;

    private CodeGeneratorXmlConfig xmlConfig;
    private TableIntrospect tableIntrospect;

    /**
     * 解析上下文
     *
     * @param configFilePath 配置文件
     * @return
     */
    public GeneratorContext resolve(String configFilePath) {
        LOGGER.info("GeneratorContextResolver resolving START, configFilePath={}", configFilePath);
        CodeGeneratorXmlConfig xmlConfig = this.parseXmlConfig(configFilePath);
        List<Entity> entityList = this.parseEntityList(xmlConfig);
        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setEntityList(entityList);
        generatorContext.setXmlConfig(xmlConfig);
        this.closeTableIntrospect();
        LOGGER.info("GeneratorContextResolver resolving END, entityList.size={}", entityList.size());
        return generatorContext;
    }

    private void initTableIntrospect() {
        CodeGeneratorXmlConfig.JdbcConnection jdbcConnection = xmlConfig.getJdbcConnection();
        Connection connection = ConnectionFactory.createConnection(jdbcConnection.getDriverClass(),
                jdbcConnection.getConnectionURL(), jdbcConnection.getUsername(), jdbcConnection.getPassword());
        this.tableIntrospect = new MysqlTableIntrospect(connection);
    }

    private void closeTableIntrospect() {
        this.tableIntrospect.closeConnection();
    }

    private List<Entity> parseEntityList(CodeGeneratorXmlConfig xmlConfig) {
        List<CodeGeneratorXmlConfig.Table> tables = xmlConfig.getTables();
        return tables.stream().map(this::table2Entity).collect(Collectors.toList());
    }

    private Entity table2Entity(CodeGeneratorXmlConfig.Table table) {
        TableInfo tableInfo = tableIntrospect.introspect(table.getTableName());
        Entity entity = new Entity();
        String lowerCamelName = GeneratorUtils.getLowerCamelName(tableInfo.getName());
        String upperCamelName = StringUtils.capitalize(lowerCamelName);
        String entityName = table.getEntityObjectName();
        String suffix = StringUtils.isNotBlank(table.getEntityObjectSuffix()) ? table.getEntityObjectSuffix() : "";
        if (StringUtils.isNotBlank(entityName)) {
            lowerCamelName = GeneratorUtils.getLowerCamelName(entityName);
            upperCamelName = StringUtils.capitalize(entityName);
        }
        entityName = upperCamelName + suffix;
        entity.setLowerCamelName(lowerCamelName);
        entity.setEntityName(entityName);
        entity.setTableName(tableInfo.getName());
        entity.setComment(tableInfo.getComment());
        entity.setUpperCamelName(upperCamelName);

        List<Field> fields = new ArrayList<>();
        fields.addAll(xmlConfig.getFixedField());
        fields.addAll(this.column2Field(table, tableInfo.getColumns()));
        this.matchIdType(fields, tableInfo.getColumns());
        entity.setFields(fields);
        entity.setHasBigDecimalField(this.hasBigDecimalField(entity.getFields()));
        return entity;
    }

    // 匹配id类型
    private void matchIdType(List<Field> fields, List<ColumnInfo> columns) {
        columns.stream().filter(x -> Objects.equals(GeneratorConst.PRIMARY_KEY_COL, x.getName()))
                .findAny().ifPresent(idColumn -> {
                    fields.removeIf(x -> Objects.equals(GeneratorConst.PRIMARY_KEY_COL, x.getColumnName()));
                    Field idField = this.convertColumnInfo2Field(idColumn);
                    idField.setNullable(true);
                    idField.setComment("ID 主键");
                    idField.setMarkColumnName(false);
                    idField.setPrimaryKey(true);
                    fields.add(0, idField);
                });
    }

    private List<Field> column2Field(CodeGeneratorXmlConfig.Table table, List<ColumnInfo> columns) {
        return columns.stream()
                .filter(x -> !table.getIgnoreColumns().contains(x.getName())) // 排除忽略字段
                .map(this::convertColumnInfo2Field).collect(Collectors.toList());
    }

    private Field convertColumnInfo2Field(ColumnInfo columnInfo) {
        Field field = new Field();
        field.setJdbcType(JdbcTypeResolver.resolve(columnInfo.getType(), columnInfo.getLength()));
        String lowerCamelName = GeneratorUtils.getLowerCamelName(columnInfo.getName());
        if (SourceVersion.isKeyword(lowerCamelName)) {
            lowerCamelName += GeneratorConst.KEY_WORD_PREFIX;
        }
        field.setLowerCamelName(lowerCamelName);
        field.setUpperCamelName(StringUtils.capitalize(lowerCamelName));
        field.setColumnName(columnInfo.getName());
        field.setLength(columnInfo.getLength());
        field.setNullable(columnInfo.getNullable());
        field.setComment(columnInfo.getComment());
        field.setMarkColumnName(!Objects.equals(field.getLowerCamelName(), field.getColumnName()));
        return field;
    }

    private boolean hasBigDecimalField(List<Field> fields) {
        return fields.stream().anyMatch(x -> JdbcType.DECIMAL.equals(x.getJdbcType()));
    }

    private CodeGeneratorXmlConfig parseXmlConfig(String configFilePath) {
        if (StringUtils.isBlank(configFilePath)) {
            configFilePath = DEFAULT_CONFIG_FILE_PATH;
        }
        LOGGER.info("GeneratorContextResolver: configFilePath={}", configFilePath);
        try {
            CodeGeneratorXmlConfig config = new CodeGeneratorXmlConfig();
            this.xmlConfig = config;
            InputStream xmlInputStream = this.getClass().getResourceAsStream(configFilePath);
            if (Objects.isNull(xmlInputStream)) {
                LOGGER.info("can't find file in resource path, try to find in file system");
                File file = new File(configFilePath);
                xmlInputStream = FileUtils.openInputStream(file);
            }
            Assert.notNull(xmlInputStream, "code generator config file is not exist: " + configFilePath);
            SAXReader reader = new SAXReader();
            reader.setValidation(true);
            Document document = reader.read(xmlInputStream);
            Element rootElement = document.getRootElement();

            config.setBaseConfig(this.getBaseConfig(rootElement));
            config.setJdbcConnection(this.getJdbcConnection(rootElement));
            this.initTableIntrospect();
            config.setJavaModelGenerator(this.getMapperGeneratorConfig(rootElement, "javaModelGenerator"));
            config.setSqlMapGenerator(this.getMapperGeneratorConfig(rootElement, "sqlMapGenerator"));
            config.setJavaClientGenerator(this.getMapperGeneratorConfig(rootElement, "javaClientGenerator"));
            config.setServiceGenerator(this.getMapperGeneratorConfig(rootElement, "serviceGenerator"));
            config.setControllerGenerator(this.getMapperGeneratorConfig(rootElement, "controllerGenerator"));
            config.setDtoGenerator(this.getMapperGeneratorConfig(rootElement, "dtoGenerator"));
            this.verifyDtoGenerator(config);
            config.setTables(this.getTables(rootElement));
            return config;
        } catch (Exception e) {
            LOGGER.error("Fail to parse xml file: " + configFilePath, e);
            throw new RuntimeException(e);
        }
    }

    private void verifyDtoGenerator(CodeGeneratorXmlConfig config) {
        CodeGeneratorXmlConfig.MapperGeneratorConfig controllerGenerator = config.getControllerGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig dtoGenerator = config.getDtoGenerator();
        if (!controllerGenerator.isDisabled()) {
            Assert.isTrue(!dtoGenerator.isDisabled(), "DTO生成配置未生效, 请检查'dtoGenerator'标签(生成controller时DTO也需要开启)");
        }
    }

    private CodeGeneratorXmlConfig.BaseConfig getBaseConfig(Element rootElement) {
        CodeGeneratorXmlConfig.BaseConfig baseConfig = new CodeGeneratorXmlConfig.BaseConfig();
        Element baseConfigEl = this.getNullableElement(rootElement, "baseConfig");
        if (Objects.nonNull(baseConfigEl)) {
            Iterator iterator = baseConfigEl.elementIterator("property");
            while (iterator.hasNext()) {
                Element property = (Element) iterator.next();
                String name = this.getAttributeValue(property, "name");
                if (StringUtils.isBlank(name)) {
                    continue;
                }
                switch (name) {
                    case "enableLombok":
                        baseConfig.setEnableLombok("true".equalsIgnoreCase(property.getStringValue()));
                        break;
                    case "enableSwagger":
                        baseConfig.setEnableSwagger("true".equalsIgnoreCase(property.getStringValue()));
                        break;
                    case "enableValidation":
                        baseConfig.setEnableValidation("true".equalsIgnoreCase(property.getStringValue()));
                        break;
                    case "enableReadWriteSeparation":
                        baseConfig.setEnableReadWriteSeparation("true".equalsIgnoreCase(property.getStringValue()));
                        break;
                    case "disableUpdatingMapperXml":
                        baseConfig.setDisableUpdatingMapperXml("true".equalsIgnoreCase(property.getStringValue()));
                        break;
                    case "enableMapperAnnotation":
                        baseConfig.setEnableMapperAnnotation("true".equalsIgnoreCase(property.getStringValue()));
                        break;
                }
            }
        }
        return baseConfig;
    }

    private CodeGeneratorXmlConfig.JdbcConnection getJdbcConnection(Element rootElement) {
        Element jdbcConnection = this.getElement(rootElement, "jdbcConnection");
        String host = this.getAttributeValueNonBlank(jdbcConnection, "host");
        String database = this.getAttributeValueNonBlank(jdbcConnection, "database");
        String tinyInt1isBit = this.getAttributeValue(jdbcConnection, "tinyInt1isBit", true);
        String queryString = StringUtils.EMPTY;
        if ("false".equalsIgnoreCase(tinyInt1isBit)) {
            queryString = "?tinyInt1isBit=false";
        }
        String connectionURL = String.format("jdbc:mysql://%s/%s%s", host, database, queryString);
        return new CodeGeneratorXmlConfig.JdbcConnection()
                .setDriverClass(this.getAttributeValueNonBlank(jdbcConnection, "driverClass"))
                .setConnectionURL(connectionURL)
                .setUsername(this.getAttributeValue(jdbcConnection, "username"))
                .setPassword(this.getAttributeValue(jdbcConnection, "password"));
    }

    private List<CodeGeneratorXmlConfig.Table> getTables(Element rootElement) {
        List<CodeGeneratorXmlConfig.Table> tables = Lists.newArrayList();
        Element tableEls = this.getElement(rootElement, "tables");
        String all = this.getAttributeValue(tableEls, "all", true);
        boolean allTables = "true".equalsIgnoreCase(all);
        String createTimeColumn = this.getAttributeValue(tableEls, "createTimeColumn", true);
        String updateTimeColumn = this.getAttributeValue(tableEls, "updateTimeColumn", true);
        String entityObjectSuffix = this.getAttributeValue(tableEls, "entityObjectSuffix", true);
        Set<String> globalIgnoreColumnSet = this.getIgnoreColumnSet(tableEls);

        String primaryKeyColumn = GeneratorConst.PRIMARY_KEY_COL;
        List<String> fixedColumns = new ArrayList<>();
        if (StringUtils.isBlank(createTimeColumn)) {
            createTimeColumn = GeneratorConst.CREATED_TIME_COL;
        }
        if (StringUtils.isBlank(updateTimeColumn)) {
            updateTimeColumn = GeneratorConst.UPDATED_TIME_COL;
        }

        fixedColumns.add(primaryKeyColumn);
        fixedColumns.add(createTimeColumn);
        fixedColumns.add(updateTimeColumn);

        xmlConfig.setFixedColumns(fixedColumns);
        List<Field> fixedFields = new ArrayList<>();
        fixedFields.add(this.getPrimaryKeyField(primaryKeyColumn));
        fixedFields.add(this.getDateField(createTimeColumn, "创建时间"));
        fixedFields.add(this.getDateField(updateTimeColumn, "修改时间"));
        xmlConfig.setFixedField(fixedFields);
        globalIgnoreColumnSet.addAll(fixedColumns);

        if (allTables) {
            List<String> tableNames = tableIntrospect.getAllTables();
            tableNames.forEach(tableName -> {
                tables.add(new CodeGeneratorXmlConfig.Table()
                        .setTableName(tableName)
                        .setIgnoreColumns(globalIgnoreColumnSet));
            });
        } else {
            Iterator iterator = tableEls.elementIterator("table");
            while (iterator.hasNext()) {
                Element tableEl = (Element) iterator.next();
                Set<String> tableIgnoreColumnSet = new HashSet<>(globalIgnoreColumnSet);
                tableIgnoreColumnSet.addAll(this.getIgnoreColumnSet(tableEl));
                tables.add(new CodeGeneratorXmlConfig.Table()
                        .setTableName(this.getAttributeValue(tableEl, "tableName"))
                        .setEntityObjectName(this.getAttributeValue(tableEl, "entityObjectName", true))
                        .setEntityObjectSuffix(entityObjectSuffix)
                        .setIgnoreColumns(tableIgnoreColumnSet));
            }
        }

        return tables;
    }

    private Field getPrimaryKeyField(String primaryKeyColumn) {
        Field field = new Field();
        field.setJdbcType(JdbcType.LONG);
        String lowerCamelName = GeneratorUtils.getLowerCamelName(primaryKeyColumn);
        if (SourceVersion.isKeyword(lowerCamelName)) {
            lowerCamelName += GeneratorConst.KEY_WORD_PREFIX;
        }
        field.setLowerCamelName(lowerCamelName);
        field.setUpperCamelName(StringUtils.capitalize(lowerCamelName));
        field.setColumnName(primaryKeyColumn);
        field.setLength(20);
        field.setNullable(true);
        field.setComment("ID 主键");
        field.setMarkColumnName(false);
        field.setPrimaryKey(true);
        return field;
    }

    private Field getDateField(String column, String comment) {
        Field field = new Field();
        field.setJdbcType(JdbcType.DATE);
        String lowerCamelName = GeneratorUtils.getLowerCamelName(column);
        if (SourceVersion.isKeyword(lowerCamelName)) {
            lowerCamelName += GeneratorConst.KEY_WORD_PREFIX;
        }
        field.setLowerCamelName(lowerCamelName);
        field.setUpperCamelName(StringUtils.capitalize(lowerCamelName));
        field.setColumnName(column);
        field.setLength(19);
        field.setNullable(true);
        field.setComment(comment);
        field.setMarkColumnName(!Objects.equals(field.getLowerCamelName(), field.getColumnName()));
        field.setSwaggerHidden(true);
        field.setIgnoreSaving(true);
        return field;
    }

    private Set<String> getIgnoreColumnSet(Element element) {
        String ignoreColumns = this.getAttributeValue(element, "ignoreColumns", true);
        Set<String> ignoreColumnSet = new HashSet<>();
        if (StringUtils.isNotBlank(ignoreColumns)) {
            ignoreColumnSet.addAll(List.of(ignoreColumns.split(",")));
        }
        return ignoreColumnSet;
    }

    private CodeGeneratorXmlConfig.MapperGeneratorConfig getMapperGeneratorConfig(Element rootElement, String nodeName) {
        Element element = rootElement.element(nodeName);
        if (Objects.isNull(element)) {
            return new CodeGeneratorXmlConfig.MapperGeneratorConfig().setDisabled(true).setModuleName(nodeName);
        }
        String disabledValue = this.getAttributeValue(element, "disabled", true);
        return new CodeGeneratorXmlConfig.MapperGeneratorConfig()
                .setDisabled("true".equalsIgnoreCase(disabledValue))
                .setTargetPackage(this.getAttributeValue(element, "targetPackage"))
                .setCodePath(this.getAttributeValue(element, "codePath"))
                .setTemplatePath(this.getAttributeValue(element, "templatePath", true))
                .setSuffix(this.getAttributeValue(element, "suffix", true))
                .setModuleName(nodeName);
    }

    private Element getElement(Element element, String name) {
        Element childElement = this.getNullableElement(element, name);
        Assert.notNull(childElement, "Element '" + name + "' is not exist");
        return childElement;
    }

    private Element getNullableElement(Element element, String name) {
        return element.element(name);
    }

    private String getAttributeValueNonBlank(Element element, String attributeName) {
        String attributeValue = this.getAttributeValue(element, attributeName, false);
        Assert.isTrue(StringUtils.isNotBlank(attributeValue), "Attribute '" + attributeName + "' is empty string");
        return attributeValue;
    }

    private String getAttributeValue(Element element, String attributeName) {
        return this.getAttributeValue(element, attributeName, false);
    }

    private String getAttributeValue(Element element, String attributeName, boolean nullable) {
        Attribute attribute = element.attribute(attributeName);
        if (!nullable) {
            Assert.notNull(attribute, "Attribute '" + attributeName + "' is not exist");
        }
        return Objects.nonNull(attribute) ? attribute.getValue() : null;
    }
}
