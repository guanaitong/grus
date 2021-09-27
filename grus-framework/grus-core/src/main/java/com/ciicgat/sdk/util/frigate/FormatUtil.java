/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by August.Zhou on 2019-09-23 13:53.
 */
class FormatUtil {
    static String formatText(List<Element> elements) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : elements) {
            if (StringUtils.isBlank(element.getKey()) || StringUtils.isBlank(element.getValue())) {
                continue;
            }
            stringBuilder.append(element.getKey()).append(":");
            if (element.isWrap()) {
                stringBuilder.append("[").append(element.getValue()).append("]");
            } else {
                stringBuilder.append(element.getValue());
            }
            if (element.isLineEnd()) {
                stringBuilder.append("\n");
            } else {
                stringBuilder.append("_");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }


    static class Element {
        private String key;
        private String value;
        private boolean lineEnd;
        private boolean wrap = true;

        Element(String key, String value) {
            this.key = key;
            this.value = value;
        }

        Element(String key, String value, boolean lineEnd) {
            this.key = key;
            this.value = value;
            this.lineEnd = lineEnd;
        }

        Element(String key, String value, boolean lineEnd, boolean wrap) {
            this.key = key;
            this.value = value;
            this.lineEnd = lineEnd;
            this.wrap = wrap;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isLineEnd() {
            return lineEnd;
        }

        public void setLineEnd(boolean lineEnd) {
            this.lineEnd = lineEnd;
        }

        public boolean isWrap() {
            return wrap;
        }

        public void setWrap(boolean wrap) {
            this.wrap = wrap;
        }
    }

}
