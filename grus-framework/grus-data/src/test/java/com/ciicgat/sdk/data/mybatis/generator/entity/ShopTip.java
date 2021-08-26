/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.entity;

import com.ciicgat.sdk.data.mybatis.generator.annotation.TableField;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableId;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableName;

import java.util.Date;

/**
 * @author Clive Yuan
 * @date 2020/12/22
 */
@TableName("ShopTip")
public class ShopTip {
    @TableId
    private Long id;
    @TableField(ignoreSaving = true)
    private Date timeCreated;
    @TableField(ignoreSaving = true)
    private Date timeModified;
    private Long ecappId;
    private Integer type;
    private String title;
    private String content;
    private Integer enable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Date timeModified) {
        this.timeModified = timeModified;
    }

    public Long getEcappId() {
        return ecappId;
    }

    public void setEcappId(Long ecappId) {
        this.ecappId = ecappId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "ShopTip{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", timeModified=" + timeModified +
                ", ecappId=" + ecappId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", enable=" + enable +
                '}';
    }
}

