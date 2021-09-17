/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg;

import com.ciicgat.grus.generator.cg.core.GeneratorChain;
import com.ciicgat.grus.generator.cg.core.GeneratorContext;
import com.ciicgat.grus.generator.cg.core.GeneratorContextResolver;

/**
 * 代码生成器
 *
 * @author Clive Yuan (jian.yuan@guanaitong.com)
 * @date 2020/11/05
 */
public class CodeGenerator {

    private final String configFilePath;


    public CodeGenerator(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public CodeGenerator() {
        this(null);
    }

    /**
     * 开始生成
     */
    public void generate() {
        // 初始化上下文解析器
        GeneratorContextResolver generatorContextResolver = new GeneratorContextResolver();
        // 解析配置上下文
        GeneratorContext generatorContext = generatorContextResolver.resolve(configFilePath);
        // 创建生成链路
        GeneratorChain generatorChain = new GeneratorChain();
        // 执行生成链路
        generatorChain.generate(generatorContext, generatorChain);
    }
}
