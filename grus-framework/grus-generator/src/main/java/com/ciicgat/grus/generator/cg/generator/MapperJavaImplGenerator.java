/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generator;

import com.ciicgat.grus.generator.cg.core.CodeGeneratorXmlConfig;
import com.ciicgat.grus.generator.cg.core.GenerateParam;
import com.ciicgat.grus.generator.cg.core.Generator;
import com.ciicgat.grus.generator.cg.core.GeneratorChain;
import com.ciicgat.grus.generator.cg.core.GeneratorConst;
import com.ciicgat.grus.generator.cg.core.GeneratorContext;
import org.springframework.beans.BeanUtils;

/**
 * Java MapperImpl 生成器
 *
 * @author Clive Yuan
 * @date 2020/12/07
 */
public class MapperJavaImplGenerator implements Generator {
    @Override
    public void generate(GeneratorContext context, GeneratorChain generatorChain) {
        CodeGeneratorXmlConfig.MapperGeneratorConfig mapperGenerator = context.getXmlConfig().getJavaClientGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig mapperImplGenerator = new CodeGeneratorXmlConfig.MapperGeneratorConfig();
        BeanUtils.copyProperties(mapperGenerator, mapperImplGenerator);
        mapperImplGenerator.setTargetPackage(mapperGenerator.getTargetPackage() + GeneratorConst.IMPL_PKG_SUFFIX);
        mapperImplGenerator.setModuleName("mapperImplGenerator");
        mapperImplGenerator.setDisabled(!context.getXmlConfig().getBaseConfig().isEnableReadWriteSeparation());
        String suffix = generatorChain.getSuffix(mapperGenerator, this);
        generatorChain.doGenerate(new GenerateParam()
                .setGenerator(this)
                .setContext(context)
                .setGeneratorConfig(mapperImplGenerator)
                .setFileNameFunction(x -> x.getUpperCamelName() + suffix + "Impl"));
    }
}
