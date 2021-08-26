/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import com.ciicgat.sdk.util.system.Systems;
import io.opentracing.Span;

import java.util.Map;

/**
 * Created by August.Zhou on 2019-09-17 10:43.
 */
public class FrigateMessage {
    /**
     * 发送渠道，默认通过1为企业微信通知
     */
    private Integer channel = 1;
    /**
     * 消息标题
     */
    private String title = "frigate 消息通知";
    /**
     * 消息内容
     */
    private String content;
    /**
     * 当有异常堆栈时，堆栈内容
     */
    private String stack;
    /**
     * 模块
     */
    private String module;
    /**
     * 标签
     */
    private Map<String, String> tags;

    /**
     * ------------------以下属于系统变量------------------------
     **/

    private String traceId;
    private String hostIp;
    private String appName;
    private String appInstance;
    private String workEnv;
    private String workIdc;
    /**
     * 发送时间
     */
    private long time;
    private boolean format = true;

    private FrigateMessage() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public boolean isFormat() {
        return format;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }

    /**
     * 把系统级别的变量都设置下
     *
     * @return
     */
    public static FrigateMessage newInstance() {
        FrigateMessage frigateMessage = new FrigateMessage();
        frigateMessage.setAppName(Systems.APP_NAME);
        frigateMessage.setAppInstance(Systems.APP_INSTANCE);
        frigateMessage.setHostIp(Systems.HOST_IP);
        frigateMessage.setWorkEnv(Systems.WORK_ENV);
        frigateMessage.setWorkIdc(Systems.WORK_IDC);
        frigateMessage.setTime(System.currentTimeMillis());
        Span span = Spans.getRootSpan();
        if (span != null && !SpanUtil.isNoop(span)) {
            frigateMessage.setTraceId(SpanUtil.getTraceId(span));
        }
        return frigateMessage;
    }

    @Override
    public String toString() {
        return com.ciicgat.grus.json.JSON.toJSONString(this);
    }
}
