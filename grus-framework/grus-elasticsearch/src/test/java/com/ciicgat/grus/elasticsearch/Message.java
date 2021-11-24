/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch;

import com.ciicgat.grus.elasticsearch.annotations.Document;
import com.ciicgat.grus.elasticsearch.annotations.Field;
import com.ciicgat.grus.elasticsearch.core.IndexAble;
import com.ciicgat.grus.elasticsearch.core.IndexSuffixType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by August.Zhou on 2019-09-11 13:12.
 */
@Document(index = "frigate-msg-", indexSuffixType = IndexSuffixType.MONTHLY, mappingPath = "/esmapper/messageMapper.json", alias = "frigate-msg")
public class Message implements Serializable, IndexAble {

    /**
     * msg 唯一标识
     */
    private String docId;

    /**
     * 当前数据所处的索引
     */
    private String docIndex;

    /**
     * 用户id列表
     */
    private String userIdList;
    private Integer channel;
    private String title = "frigate 消息通知";
    private String content;
    private String stack;
    private String hostIp;
    private String traceId;
    private String appName;
    @Field(ignore = true)
    private String appInstance;
    private String workEnv;
    private String workIdc;
    private String module;


    private Map<String, String> tags;


    /**
     * 消息渠道来源
     * 应用程序通知、运维报警
     */

    private String source;
    /**
     * 消息处理状态
     * 0未处理
     * 1已处理
     */
    private Integer status;

    /**
     * 信息创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", locale = "zh", timezone = "GMT+8")
    private Date timeCreated;

    private boolean oldWay;

    public boolean isOldWay() {
        return oldWay;
    }

    public void setOldWay(boolean oldWay) {
        this.oldWay = oldWay;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String id) {
        this.docId = id;
    }

    @Override
    public String getDocIndex() {
        return docIndex;
    }

    @Override
    public void setDocIndex(String index) {
        this.docIndex = index;
    }

    public String getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(String userIdList) {
        this.userIdList = userIdList;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
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

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppInstance() {
        return appInstance;
    }

    public void setAppInstance(String appInstance) {
        this.appInstance = appInstance;
    }

    public String getWorkEnv() {
        return workEnv;
    }

    public void setWorkEnv(String workEnv) {
        this.workEnv = workEnv;
    }

    public String getWorkIdc() {
        return workIdc;
    }

    public void setWorkIdc(String workIdc) {
        this.workIdc = workIdc;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public Supplier<Date> getTimestampSupplier() {
        return () -> this.timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (oldWay != message.oldWay) return false;
        if (docId != null ? !docId.equals(message.docId) : message.docId != null) return false;
        if (docIndex != null ? !docIndex.equals(message.docIndex) : message.docIndex != null) return false;
        if (userIdList != null ? !userIdList.equals(message.userIdList) : message.userIdList != null) return false;
        if (channel != null ? !channel.equals(message.channel) : message.channel != null) return false;
        if (title != null ? !title.equals(message.title) : message.title != null) return false;
        if (content != null ? !content.equals(message.content) : message.content != null) return false;
        if (stack != null ? !stack.equals(message.stack) : message.stack != null) return false;
        if (hostIp != null ? !hostIp.equals(message.hostIp) : message.hostIp != null) return false;
        if (traceId != null ? !traceId.equals(message.traceId) : message.traceId != null) return false;
        if (appName != null ? !appName.equals(message.appName) : message.appName != null) return false;
        if (appInstance != null ? !appInstance.equals(message.appInstance) : message.appInstance != null) return false;
        if (workEnv != null ? !workEnv.equals(message.workEnv) : message.workEnv != null) return false;
        if (workIdc != null ? !workIdc.equals(message.workIdc) : message.workIdc != null) return false;
        if (module != null ? !module.equals(message.module) : message.module != null) return false;
        if (tags != null ? !tags.equals(message.tags) : message.tags != null) return false;
        if (source != null ? !source.equals(message.source) : message.source != null) return false;
        if (status != null ? !status.equals(message.status) : message.status != null) return false;
        return timeCreated != null ? timeCreated.equals(message.timeCreated) : message.timeCreated == null;
    }

    @Override
    public int hashCode() {
        int result = docId != null ? docId.hashCode() : 0;
        result = 31 * result + (docIndex != null ? docIndex.hashCode() : 0);
        result = 31 * result + (userIdList != null ? userIdList.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (stack != null ? stack.hashCode() : 0);
        result = 31 * result + (hostIp != null ? hostIp.hashCode() : 0);
        result = 31 * result + (traceId != null ? traceId.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (appInstance != null ? appInstance.hashCode() : 0);
        result = 31 * result + (workEnv != null ? workEnv.hashCode() : 0);
        result = 31 * result + (workIdc != null ? workIdc.hashCode() : 0);
        result = 31 * result + (module != null ? module.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (timeCreated != null ? timeCreated.hashCode() : 0);
        result = 31 * result + (oldWay ? 1 : 0);
        return result;
    }
}
