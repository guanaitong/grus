/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generated.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * 推广员
 */
public class Test2VO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date timeCreated;

    /**
     * 修改时间
     */
    private Date timeModified;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 邀请配置
     */
    private String invitationConfig;

    /**
     * mapper
     */
    private Integer mapper;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getInvitationConfig() {
        return invitationConfig;
    }

    public void setInvitationConfig(String invitationConfig) {
        this.invitationConfig = invitationConfig;
    }

    public Integer getMapper() {
        return mapper;
    }

    public void setMapper(Integer mapper) {
        this.mapper = mapper;
    }

    @Override
    public String toString() {
        return "Test2DO{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", timeModified=" + timeModified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", adminId=" + adminId +
                ", userId=" + userId +
                ", invitationConfig=" + invitationConfig +
                ", mapper=" + mapper +
                '}';
    }
}
