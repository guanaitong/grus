/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;


import java.util.List;

/**
 * 上下文
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class GeneratorContext {
    /**
     * 实体列表
     */
    private List<Entity> entityList;
    /**
     * 配置
     */
    private CodeGeneratorXmlConfig xmlConfig;

    public List<Entity> getEntityList() {
        return entityList;
    }

    public GeneratorContext setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
        return this;
    }

    public CodeGeneratorXmlConfig getXmlConfig() {
        return xmlConfig;
    }

    public GeneratorContext setXmlConfig(CodeGeneratorXmlConfig xmlConfig) {
        this.xmlConfig = xmlConfig;
        return this;
    }
}
