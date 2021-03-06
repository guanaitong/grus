/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generator;

import com.ciicgat.grus.generator.cg.core.GenerateParam;
import com.ciicgat.grus.generator.cg.core.Generator;
import com.ciicgat.grus.generator.cg.core.GeneratorChain;
import com.ciicgat.grus.generator.cg.core.GeneratorContext;

/**
 * Java Mapper 生成器
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class MapperJavaGenerator implements Generator {
    @Override
    public void generate(GeneratorContext context, GeneratorChain generatorChain) {
        generatorChain.doGenerate(new GenerateParam()
                .setGenerator(this)
                .setContext(context)
                .setGeneratorConfig(context.getXmlConfig().getJavaClientGenerator()));
    }
}
