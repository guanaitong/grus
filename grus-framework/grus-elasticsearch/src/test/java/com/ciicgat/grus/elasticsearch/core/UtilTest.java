/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Created by August.Zhou on 2024/4/22 10:08
 */
class UtilTest {

    @Test
    void getFileContent() {
        String fileContent = Util.getFileContent("esmapper/messageMapper.json");
        Assertions.assertFalse(fileContent.isEmpty());

        String fileContent1 = Util.getFileContent("/esmapper/messageMapper.json");
        Assertions.assertFalse(fileContent1.isEmpty());
        Assertions.assertThrows(EsDataException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Util.getFileContent("/esmapper/messageMapper.jsonxxx");
            }
        });
    }
}
