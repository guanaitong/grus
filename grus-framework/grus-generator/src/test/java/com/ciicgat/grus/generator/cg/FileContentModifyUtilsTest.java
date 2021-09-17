/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg;

import com.ciicgat.grus.generator.cg.util.FileContentModifyUtils;
import org.junit.Test;

/**
 * @author Clive Yuan
 * @date 2021/01/06
 */
public class FileContentModifyUtilsTest {

    @Test
    public void modify() {
        String fileName = "/Users/cliveyuan/data/code/gat/cg/grus-generator/src/test/resources/generated/mapper/Test2Mapper.xml";
        String start = "<sql id=\"BaseColumnList\">";
        String end = "</sql>";
        FileContentModifyUtils.modify(fileName, start, end, "    <sql id=\"BaseColumnList\">\n" +
                "        `id`,`timeCreated`,`timeModified2`\n" +
                "    </sql>");
    }

    @Test
    public void modify2() {
        String fileName = "/Users/cliveyuan/data/code/gat/cg/grus-generator/src/test/resources/generated/mapper/Test2Mapper.xml";
        String start = "<resultMap id=\"BaseResultMap\"";
        String end = "</resultMap>";
        FileContentModifyUtils.modify(fileName, start, end, "    <resultMap id=\"BaseResultMap\" type=\"com.ciicgat.grusgenerator.test.cg.generated.entity.Test2\">\n" +
                "        <id column=\"id\" property=\"id\"/>\n" +
                "        <result column=\"timeCreated\" property=\"timeCreated\"/>\n" +
                "        <result column=\"timeModified\" property=\"timeModified\"/>\n" +
                "    </resultMap>");
    }

    // @Test
    // public void modifyXml() {
    //     String fileName = "/Users/cliveyuan/data/code/gat/cg/grus-generator/src/test/resources/generated/mapper/Test2Mapper.xml";
    //     String xpath = "//sql[@id='BaseColumnList']";
    //     FileContentModifyUtils.modifyXml(fileName, xpath, "clive,tina,tudou");
    // }
}
