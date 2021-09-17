/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

/**
 * 文件类型
 *
 * @author Clive Yuan
 * @date 2020/11/06
 */
public enum FileType {

    JAVA(".java"),
    XML(".xml");

    /**
     * 扩展名
     */
    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
