/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.model;

import com.ciicgat.grus.json.SimpleDateDeserializer;
import com.ciicgat.grus.json.SimpleDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * // TODO 如何引入 swagger，maven 依赖不确定
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-05-05 14:41
 */
public class MvcDateBeanRequest {

    @NotBlank(message = "文案不能为空")
    private String text;

    /**
     * 因为使用了继承特性，所以基本上入参和返回如果有序列化规则的话，序列化和反序列化规则都要写。<br/>
     * 针对客户端是序列还，针对服务端是反序列化。
     */
    @JsonSerialize(using = SimpleDateSerializer.class)
    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date date;

    public MvcDateBeanRequest(String text, Date date) {
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
