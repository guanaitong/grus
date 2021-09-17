/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

/**
 * 代码生成器接口
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public interface Generator {

    /**
     * 生成
     *
     * @param context 上下文
     * @param generatorChain 生成器链
     */
    void generate(GeneratorContext context, GeneratorChain generatorChain);
}
