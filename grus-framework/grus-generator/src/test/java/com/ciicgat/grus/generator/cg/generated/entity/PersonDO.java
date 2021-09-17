/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generated.entity;

import com.ciicgat.sdk.data.mybatis.generator.annotation.TableField;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableId;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 个人
 */
@TableName("Person")
public class PersonDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID 主键
     */
    @TableId
    private Integer id;

    /**
     * 创建时间
     */
    @TableField(ignoreSaving = true)
    private Date timeCreated;

    /**
     * 修改时间
     */
    @TableField(ignoreSaving = true)
    private Date timeModified;

    /**
     * 姓名
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    /**
     * 年龄
     */
    private Integer age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "PersonDO{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", timeModified=" + timeModified +
                ", name=" + name +
                ", content=" + content +
                ", age=" + age +
                '}';
    }
}
