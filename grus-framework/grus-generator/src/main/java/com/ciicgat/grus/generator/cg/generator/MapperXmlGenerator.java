/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generator;

import com.ciicgat.grus.generator.cg.core.ExtensionParam;
import com.ciicgat.grus.generator.cg.core.GenerateParam;
import com.ciicgat.grus.generator.cg.core.Generator;
import com.ciicgat.grus.generator.cg.core.GeneratorChain;
import com.ciicgat.grus.generator.cg.core.GeneratorContext;
import com.ciicgat.grus.generator.cg.core.GeneratorEnum;
import com.ciicgat.grus.generator.cg.util.FileContentModifyUtils;
import com.ciicgat.grus.generator.cg.util.FreemarkerUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * Xml Mapper 生成器
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class MapperXmlGenerator implements Generator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapperXmlGenerator.class);

    @Override
    public void generate(GeneratorContext context, GeneratorChain generatorChain) {
        generatorChain.doGenerate(new GenerateParam()
                .setGenerator(this)
                .setContext(context)
                .setGeneratorConfig(context.getXmlConfig().getSqlMapGenerator())
                .setBeforeExtensionHandle(this::modifyExistMapperXml));
    }

    private void modifyExistMapperXml(ExtensionParam extensionParam) {
        if (extensionParam.getContext().getXmlConfig().getBaseConfig().isDisableUpdatingMapperXml()) {
            LOGGER.info("modifyExistMapperXml disableUpdatingMapperXml=true");
            return;
        }
        extensionParam.getContext().getEntityList().forEach(entity -> {
            String fileName = entity.getFileName() + GeneratorEnum.MAPPER_XML.getFileType().getExtension();
            String filePath = String.format("%s/%s", extensionParam.getCodeFilePath(), fileName);
            // 判断文件是否存在
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            Map<String, Object> paramMap = extensionParam.getParamMap();
            paramMap.put("entity", entity);

            // 修改BaseColumnList
            this.modifyBaseColumnList(paramMap, filePath);
            // 修改BaseResultMap
            this.modifyBaseResultMap(paramMap, filePath);
        });
    }

    private void modifyBaseColumnList(Map<String, Object> paramMap, String filePath) {
        if (this.containAtSql(filePath)) {
            LOGGER.info("modifyBaseColumnList filePath={}, containAtSql=true", filePath);
            return;
        }
        this.modifyContent(paramMap, filePath, "templates/segment/base_column_list.inc.ftl", "<sql id=\"BaseColumnList\">", "</sql>");
    }

    private void modifyBaseResultMap(Map<String, Object> paramMap, String filePath) {
        this.modifyContent(paramMap, filePath, "templates/segment/base_result_map.inc.ftl", "<resultMap id=\"BaseResultMap\"", "</resultMap>");
    }

    private void modifyContent(Map<String, Object> paramMap, String filePath, String ftlPath, String start, String end) {
        String codeContent = FreemarkerUtils.parseTemplate(ftlPath, paramMap);
        FileContentModifyUtils.modify(filePath, start, end, String.format("    %s", codeContent.trim()));
    }

    // 判断BaseColumnList是否包含<!--@sql select -->
    private boolean containAtSql(String filePath) {
        try {
            File file = new File(filePath);
            SAXReader reader = new SAXReader(); // NOSONAR
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            Node node = rootElement.selectSingleNode("//sql[@id='BaseColumnList']/comment()");
            if (Objects.isNull(node)) {
                return false;
            }
            return node.getText().contains("@sql select");
        } catch (Exception e) {
            LOGGER.error("containAtSql", e);
            return false;
        }
    }
}
