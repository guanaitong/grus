/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg;

import org.junit.jupiter.api.Test;

/**
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class CodeGeneratorTest {

    @Test
    public void generate() {
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generate();
    }
}
