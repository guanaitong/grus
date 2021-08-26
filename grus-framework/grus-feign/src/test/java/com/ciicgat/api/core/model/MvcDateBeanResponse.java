/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.model;

import com.ciicgat.grus.json.SimpleDateDeserializer;
import com.ciicgat.grus.json.SimpleDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-05-05 14:43
 */
public class MvcDateBeanResponse {

    private String text;
    @JsonDeserialize(using = SimpleDateDeserializer.class)
    @JsonSerialize(using = SimpleDateSerializer.class)
    private Date date;

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
