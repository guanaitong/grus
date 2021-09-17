/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import com.ciicgat.grus.generator.cg.generator.ControllerGenerator;
import com.ciicgat.grus.generator.cg.generator.DTOGenerator;
import com.ciicgat.grus.generator.cg.generator.EntityGenerator;
import com.ciicgat.grus.generator.cg.generator.MapperJavaGenerator;
import com.ciicgat.grus.generator.cg.generator.MapperJavaImplGenerator;
import com.ciicgat.grus.generator.cg.generator.MapperXmlGenerator;
import com.ciicgat.grus.generator.cg.generator.ServiceGenerator;
import com.ciicgat.grus.generator.cg.generator.ServiceImplGenerator;

import java.util.Arrays;
import java.util.Objects;

/**
 * 生成器注册枚举类
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public enum GeneratorEnum {

    ENTITY(EntityGenerator.class, FileType.JAVA, "templates/entity.java.ftl", true, ""),
    MAPPER_XML(MapperXmlGenerator.class, FileType.XML, "templates/mapper.xml.ftl", false, "Mapper"),
    MAPPER_JAVA(MapperJavaGenerator.class, FileType.JAVA, "templates/mapper.java.ftl", false, "Mapper"),
    MAPPER_JAVA_IMPL(MapperJavaImplGenerator.class, FileType.JAVA, "templates/mapperimpl.java.ftl", false, "Mapper"),
    SERVICE(ServiceGenerator.class, FileType.JAVA, "templates/service.java.ftl", false, "Service"),
    SERVICE_IMPL(ServiceImplGenerator.class, FileType.JAVA, "templates/serviceimpl.java.ftl", false, "Service"),
    DTO(DTOGenerator.class, FileType.JAVA, "templates/dto.java.ftl", false, "DTO"),
    CONTROLLER(ControllerGenerator.class, FileType.JAVA, "templates/controller.java.ftl", false, "Controller");

    /**
     * 生成器类
     */
    private final Class<?> clazz;
    /**
     * 文件类型
     */
    private final FileType fileType;
    /**
     * 默认模板路径
     */
    private final String templatePath;
    /**
     * 是否覆盖写入
     */
    private final boolean overwrite;
    /**
     * 默认后缀
     */
    private final String defaultSuffix;

    GeneratorEnum(Class<?> clazz, FileType fileType, String templatePath, boolean overwrite, String defaultSuffix) {
        this.clazz = clazz;
        this.fileType = fileType;
        this.templatePath = templatePath;
        this.overwrite = overwrite;
        this.defaultSuffix = defaultSuffix;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public String getDefaultSuffix() {
        return defaultSuffix;
    }

    public static GeneratorEnum valueOf(Generator generator) {
        return Arrays.stream(GeneratorEnum.values()).filter(x -> Objects.equals(generator.getClass(), x.getClazz()))
                .findAny().orElseThrow();
    }
}
