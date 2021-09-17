/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generator;

import com.ciicgat.grus.generator.cg.core.Generator;
import com.ciicgat.grus.generator.cg.core.CodeGeneratorXmlConfig;
import com.ciicgat.grus.generator.cg.core.GenerateParam;
import com.ciicgat.grus.generator.cg.core.GeneratorChain;
import com.ciicgat.grus.generator.cg.core.GeneratorConst;
import com.ciicgat.grus.generator.cg.core.GeneratorContext;
import org.springframework.beans.BeanUtils;

/**
 * Service 实现 生成器
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class ServiceImplGenerator implements Generator {
    @Override
    public void generate(GeneratorContext context, GeneratorChain generatorChain) {
        CodeGeneratorXmlConfig.MapperGeneratorConfig serviceGenerator = context.getXmlConfig().getServiceGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig serviceImplGenerator = new CodeGeneratorXmlConfig.MapperGeneratorConfig();
        BeanUtils.copyProperties(serviceGenerator, serviceImplGenerator);
        serviceImplGenerator.setTargetPackage(serviceGenerator.getTargetPackage() + GeneratorConst.IMPL_PKG_SUFFIX);
        serviceImplGenerator.setModuleName("serviceImplGenerator");
        String suffix = generatorChain.getSuffix(serviceGenerator, this);
        generatorChain.doGenerate(new GenerateParam()
                .setGenerator(this)
                .setContext(context)
                .setGeneratorConfig(serviceImplGenerator)
                .setFileNameFunction(x -> x.getUpperCamelName() + suffix + "Impl"));
    }
}
