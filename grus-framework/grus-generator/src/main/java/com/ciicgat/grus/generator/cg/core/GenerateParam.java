/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 生成参数
 *
 * @author Clive Yuan
 * @date 2020/11/06
 */
public class GenerateParam implements Serializable {
    /**
     * 上下文
     */
    private GeneratorContext context;
    /**
     * 文件名方法
     */
    private Function<Entity, String> fileNameFunction;
    /**
     * 生成器
     */
    private Generator generator;
    /**
     * 生成配置
     */
    private CodeGeneratorXmlConfig.MapperGeneratorConfig generatorConfig;
    /**
     * 生成前 扩展处理
     */
    private Consumer<ExtensionParam> beforeExtensionHandle;

    public GeneratorContext getContext() {
        return context;
    }

    public GenerateParam setContext(GeneratorContext context) {
        this.context = context;
        return this;
    }

    public Function<Entity, String> getFileNameFunction() {
        return fileNameFunction;
    }

    public GenerateParam setFileNameFunction(Function<Entity, String> fileNameFunction) {
        this.fileNameFunction = fileNameFunction;
        return this;
    }

    public Generator getGenerator() {
        return generator;
    }

    public GenerateParam setGenerator(Generator generator) {
        this.generator = generator;
        return this;
    }

    public CodeGeneratorXmlConfig.MapperGeneratorConfig getGeneratorConfig() {
        return generatorConfig;
    }

    public GenerateParam setGeneratorConfig(CodeGeneratorXmlConfig.MapperGeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
        return this;
    }

    public Consumer<ExtensionParam> getBeforeExtensionHandle() {
        return beforeExtensionHandle;
    }

    public GenerateParam setBeforeExtensionHandle(Consumer<ExtensionParam> beforeExtensionHandle) {
        this.beforeExtensionHandle = beforeExtensionHandle;
        return this;
    }
}
