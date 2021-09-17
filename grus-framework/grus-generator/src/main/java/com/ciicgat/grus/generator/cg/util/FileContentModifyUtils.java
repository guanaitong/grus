/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件内容修改工具
 *
 * @author Clive Yuan
 * @date 2021/01/06
 */
public class FileContentModifyUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileContentModifyUtils.class);
    public static void modify(String fileName, String startMark, String endMark, String content) {
        try {
            List<String> newLines = new ArrayList<>();
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            int start = -1;
            int end = -1;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.startsWith(startMark)) {
                    start = i;
                }
                if (line.contains(endMark)) {
                    end = i;
                    if (start >= 0) {
                        break;
                    }
                }
            }
            LOGGER.info("FileContentModifyUtils.modify start={}, end={}", start, end);
            if (start >= 0 && end >= 0) {
                for (int i = 0; i < lines.size(); i++) {
                    if (i < start || i > end) {
                        newLines.add(lines.get(i));
                    }
                }

                newLines.add(start, content);
                Files.write(Paths.get(fileName), newLines, StandardCharsets.UTF_8);
            } else {
                LOGGER.info("FileContentModifyUtils.modify can't find the segment to modify");
            }
        } catch (IOException e) {
            LOGGER.error("FileContentModifyUtils.modify error", e);
        }
    }
}
