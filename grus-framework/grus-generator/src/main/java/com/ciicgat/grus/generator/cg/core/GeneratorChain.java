/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import com.ciicgat.grus.generator.cg.util.FreemarkerUtils;
import com.ciicgat.grus.generator.cg.util.ReflectUtils;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 生成器链
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class GeneratorChain implements Generator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorChain.class);

    /**
     * 生成器集合
     */
    private final List<Generator> generators = new ArrayList<>();

    /**
     * 执行下标
     */
    private int index = 0;

    public GeneratorChain() {
        this.init();
    }

    @Override
    public void generate(GeneratorContext context, GeneratorChain generatorChain) {
        if (generators.size() == index) {
            return;
        }
        Generator generator = generators.get(index);
        LOGGER.info("GeneratorChain generate: generator={}, index={}", generator.getClass().getSimpleName(), index);
        index++;
        generator.generate(context, generatorChain);
    }

    /**
     * 初始化: 从生成器注册枚举中读取生成器并将其初始化然后加入列表
     */
    private void init() {
        LOGGER.info("GeneratorChain init START");
        for (GeneratorEnum value : GeneratorEnum.values()) {
            generators.add(ReflectUtils.newInstance(value.getClazz()));
        }
        LOGGER.info("GeneratorChain init END, generators.size={}", generators.size());
    }

    /**
     * 生成文件
     *
     * @param generateParam 生成参数
     */
    public void doGenerate(GenerateParam generateParam) {
        try {
            CodeGeneratorXmlConfig.MapperGeneratorConfig generatorConfig = generateParam.getGeneratorConfig();
            if (generatorConfig.isDisabled()) {
                LOGGER.info("doGenerate skip the module: {}", generatorConfig.getModuleName());
                return;
            }
            GeneratorEnum generatorEnum = GeneratorEnum.valueOf(generateParam.getGenerator());
            GeneratorContext context = generateParam.getContext();
            // 设置文件名
            this.setFileName(generateParam);
            // 文件保存路径
            String codeFilePath = this.getCodeFilePath(generatorConfig);
            // 模板变量
            Map<String, Object> paramMap = this.getParamMap(context);
            String customizedTemplatePath = generatorConfig.getTemplatePath();
            // 代码模板: 如果自定义为空 则使用默认
            String templatePath = StringUtils.isNotBlank(customizedTemplatePath) ?
                    customizedTemplatePath : generatorEnum.getTemplatePath();

            if (Objects.nonNull(generateParam.getBeforeExtensionHandle())) {
                generateParam.getBeforeExtensionHandle().accept(new ExtensionParam()
                        .setContext(generateParam.getContext())
                        .setParamMap(paramMap)
                        .setCodeFilePath(codeFilePath));
            }
            // 遍历实体列表,生成文件
            context.getEntityList().forEach(entity -> this.generateFile(paramMap, templatePath, codeFilePath, entity, generatorEnum));

        } finally {
            // 继续责任链
            this.generate(generateParam.getContext(), this);
        }
    }

    public String getSuffix(CodeGeneratorXmlConfig.MapperGeneratorConfig generatorConfig, Generator generator) {
        return getSuffix(generatorConfig, GeneratorEnum.valueOf(generator));
    }

    public String getSuffix(CodeGeneratorXmlConfig.MapperGeneratorConfig generatorConfig, GeneratorEnum generatorEnum) {
        String suffix = generatorEnum.getDefaultSuffix();
        if (StringUtils.isNotBlank(generatorConfig.getSuffix())) {
            suffix = generatorConfig.getSuffix();
        }
        return suffix;
    }

    private void setFileName(GenerateParam generateParam) {
        Function<Entity, String> function = generateParam.getFileNameFunction();
        List<Entity> entities = generateParam.getContext().getEntityList();

        if (Objects.nonNull(function)) {
            // 处理自定义文件名
            entities.forEach(x -> x.setFileName(function.apply(x)));
        } else {
            // 默认为 大小驼峰+后缀
            String suffix = this.getSuffix(generateParam.getGeneratorConfig(), generateParam.getGenerator());
            entities.forEach(x -> x.setFileName(x.getUpperCamelName() + suffix));
        }
    }

    private String getCodeFilePath(CodeGeneratorXmlConfig.MapperGeneratorConfig generatorConfig) {
        String targetPackage = generatorConfig.getTargetPackage();
        String projectPath = System.getProperty("user.dir");
        String codePath = generatorConfig.getCodePath();
        String packagePath = targetPackage.replace(".", "/");
        return String.format("%s/%s/%s", projectPath, codePath, packagePath);
    }

    private Map<String, Object> getParamMap(GeneratorContext context) {
        CodeGeneratorXmlConfig xmlConfig = context.getXmlConfig();
        CodeGeneratorXmlConfig.MapperGeneratorConfig javaModelGenerator = xmlConfig.getJavaModelGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig javaClientGenerator = xmlConfig.getJavaClientGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig serviceGenerator = xmlConfig.getServiceGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig controllerGenerator = xmlConfig.getControllerGenerator();
        CodeGeneratorXmlConfig.MapperGeneratorConfig dtoGenerator = xmlConfig.getDtoGenerator();
        String dtoSuffix = this.getSuffix(dtoGenerator, GeneratorEnum.DTO);
        String mapperSuffix = this.getSuffix(javaClientGenerator, GeneratorEnum.MAPPER_JAVA);
        String serviceSuffix = this.getSuffix(serviceGenerator, GeneratorEnum.SERVICE);
        // 设置变量
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("entityPackage", javaModelGenerator.getTargetPackage());
        paramMap.put("mapperPackage", javaClientGenerator.getTargetPackage());
        paramMap.put("mapperImplPackage", javaClientGenerator.getTargetPackage() + GeneratorConst.IMPL_PKG_SUFFIX);
        paramMap.put("servicePackage", serviceGenerator.getTargetPackage());
        paramMap.put("serviceImplPackage", serviceGenerator.getTargetPackage() + GeneratorConst.IMPL_PKG_SUFFIX);
        paramMap.put("controllerPackage", controllerGenerator.getTargetPackage());
        paramMap.put("dtoPackage", dtoGenerator.getTargetPackage());
        paramMap.put("baseConfig", xmlConfig.getBaseConfig());
        paramMap.put("dtoSuffix", dtoSuffix);
        paramMap.put("mapperSuffix", mapperSuffix);
        paramMap.put("serviceSuffix", serviceSuffix);
        return paramMap;
    }

    private void generateFile(Map<String, Object> paramMap, String ftlPath, String codeFilePath,
                              Entity entity, GeneratorEnum generatorEnum) {
        String fileName = entity.getFileName() + generatorEnum.getFileType().getExtension();
        paramMap.put("entity", entity);
        // 通过模板生成代码内容
        String codeContent = FreemarkerUtils.parseTemplate(ftlPath, paramMap);
        String filePath = String.format("%s/%s", codeFilePath, fileName);
        // 写入指定目录
        this.writeStringToFile(filePath, codeContent, generatorEnum.isOverwrite());
    }

    private void writeStringToFile(String filePath, String data, boolean overwrite) {
        try {
            File file = new File(filePath);
            if (file.exists() && !overwrite) {
                LOGGER.debug("The file is exist, skip creating it: {}", file.getName());
                return;
            }
            FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);
            LOGGER.debug("The file was created successfully: {}", file.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
