/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

/**
 * Created by Albert on 2019-05-08.
 */
public class MessagePacket {

    private String msgtype;

    private Text text;
    private Markdown markdown;

    public MessagePacket() {
    }

    public MessagePacket(String msgtype, Text text) {
        this.msgtype = msgtype;
        this.text = text;
    }

    public MessagePacket(String msgtype, Markdown markdown) {
        this.msgtype = msgtype;
        this.markdown = markdown;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    @Override
    public String toString() {

        StringBuilder messageBody = new StringBuilder();

        messageBody.append("{");
        messageBody.append("\"").append("msgtype").append("\"").append(":").append("\"").append(msgtype).append("\"").append(",");

        if (null != text) {
            messageBody.append("\"").append("text").append("\"").append(":");
            messageBody.append("{")
                    .append("\"").append("content").append("\"").append(":").append("\"").append(text.getContent()).append("\"").append(",")
                    .append("\"").append("mentioned_list").append("\"").append(":").append("[\"").append("@all").append("\"]")
                    .append("}");
        }

        if (null != markdown) {
            messageBody.append("\"").append("markdown").append("\"").append(":");
            messageBody.append("{")
                    .append("\"").append("content").append("\"").append(":").append("\"").append(markdown.getContent()).append("\"")
                    .append("}");
        }


        messageBody.append("}");
        return messageBody.toString();
    }

    public static final class Text {
        private String content;

        public Text() {
        }

        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static final class Markdown {

        private String content;

        public Markdown() {
        }

        public Markdown(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
