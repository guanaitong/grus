/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generated.entity;

import com.ciicgat.sdk.data.mybatis.generator.annotation.TableField;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableId;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 测试
 */
@TableName("Test")
public class TestDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID 主键
     */
    @TableId
    private Long id;

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
     * 标题
     */
    private String title;

    /**
     * boo
     */
    private Boolean boo;

    /**
     * blob
     */
    private byte[] blob;

    /**
     * medium_int
     */
    @TableField(value = "medium_int")
    private Integer mediumInt;

    /**
     * float
     */
    @TableField(value = "float")
    private Float float$;

    /**
     * decimal
     */
    private BigDecimal decimal;

    /**
     * DATE
     */
    @TableField(value = "DATE")
    private Date date;

    /**
     * DATETIME
     */
    @TableField(value = "DATETIME")
    private Date datetime;

    /**
     * TIMESTAMP
     */
    @TableField(value = "TIMESTAMP")
    private Date timestamp;

    /**
     * TIME
     */
    @TableField(value = "TIME")
    private Date time;

    /**
     * YEAR
     */
    @TableField(value = "YEAR")
    private Date year;

    /**
     * mobile
     */
    private String mobile;

    /**
     * TEXT
     */
    @TableField(value = "TEXT")
    private String text;

    /**
     * LONGTEXT
     */
    @TableField(value = "LONGTEXT")
    private String longtext;

    /**
     * enum
     */
    @TableField(value = "enum")
    private String enum$;

    /**
     * set
     */
    private String set;

    /**
     * public
     */
    @TableField(value = "public")
    private Integer public$;

    /**
     * withQuery
     */
    private Integer withQuery;

    /**
     * tinyint3
     */
    private Integer tinyint3;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getBoo() {
        return boo;
    }

    public void setBoo(Boolean boo) {
        this.boo = boo;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public Integer getMediumInt() {
        return mediumInt;
    }

    public void setMediumInt(Integer mediumInt) {
        this.mediumInt = mediumInt;
    }

    public Float getFloat$() {
        return float$;
    }

    public void setFloat$(Float float$) {
        this.float$ = float$;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal) {
        this.decimal = decimal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getYear() {
        return year;
    }

    public void setYear(Date year) {
        this.year = year;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLongtext() {
        return longtext;
    }

    public void setLongtext(String longtext) {
        this.longtext = longtext;
    }

    public String getEnum$() {
        return enum$;
    }

    public void setEnum$(String enum$) {
        this.enum$ = enum$;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public Integer getPublic$() {
        return public$;
    }

    public void setPublic$(Integer public$) {
        this.public$ = public$;
    }

    public Integer getWithQuery() {
        return withQuery;
    }

    public void setWithQuery(Integer withQuery) {
        this.withQuery = withQuery;
    }

    public Integer getTinyint3() {
        return tinyint3;
    }

    public void setTinyint3(Integer tinyint3) {
        this.tinyint3 = tinyint3;
    }

    @Override
    public String toString() {
        return "TestDO{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", timeModified=" + timeModified +
                ", title=" + title +
                ", boo=" + boo +
                ", blob=" + blob +
                ", mediumInt=" + mediumInt +
                ", float$=" + float$ +
                ", decimal=" + decimal +
                ", date=" + date +
                ", datetime=" + datetime +
                ", timestamp=" + timestamp +
                ", time=" + time +
                ", year=" + year +
                ", mobile=" + mobile +
                ", text=" + text +
                ", longtext=" + longtext +
                ", enum$=" + enum$ +
                ", set=" + set +
                ", public$=" + public$ +
                ", withQuery=" + withQuery +
                ", tinyint3=" + tinyint3 +
                '}';
    }
}
