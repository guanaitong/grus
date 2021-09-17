/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generator;

import com.ciicgat.grus.generator.cg.core.Generator;
import com.ciicgat.grus.generator.cg.core.CodeGeneratorXmlConfig;
import com.ciicgat.grus.generator.cg.core.GenerateParam;
import com.ciicgat.grus.generator.cg.core.GeneratorChain;
import com.ciicgat.grus.generator.cg.core.GeneratorContext;

/**
 * DTO 生成器
 *
 * @author Clive Yuan
 * @date 2020/12/07
 */
public class DTOGenerator implements Generator {

    @Override
    public void generate(GeneratorContext context, GeneratorChain generatorChain) {
        CodeGeneratorXmlConfig.MapperGeneratorConfig controllerGenerator = context.getXmlConfig().getControllerGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig dtoGenerator = context.getXmlConfig().getDtoGenerator();
        // dto 依附于controller, 若不生成controller, 则不生成dto
        dtoGenerator.setDisabled(controllerGenerator.isDisabled());
        generatorChain.doGenerate(new GenerateParam()
                .setGenerator(this)
                .setContext(context)
                .setGeneratorConfig(dtoGenerator));
    }


}
