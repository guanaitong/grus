/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import java.util.Map;

/**
 * @author Clive Yuan
 * @date 2021/01/07
 */
public class ExtensionParam {
    /**
     * 生成上下文
     */
    private GeneratorContext context;
    /**
     * 模板参数
     */
    private Map<String, Object> paramMap;
    /**
     * 代码文件保存路径
     */
    private String codeFilePath;

    public GeneratorContext getContext() {
        return context;
    }

    public ExtensionParam setContext(GeneratorContext context) {
        this.context = context;
        return this;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public ExtensionParam setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
        return this;
    }

    public String getCodeFilePath() {
        return codeFilePath;
    }

    public ExtensionParam setCodeFilePath(String codeFilePath) {
        this.codeFilePath = codeFilePath;
        return this;
    }
}
