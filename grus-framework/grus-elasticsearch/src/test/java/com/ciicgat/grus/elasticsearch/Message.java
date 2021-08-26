/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
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

}
