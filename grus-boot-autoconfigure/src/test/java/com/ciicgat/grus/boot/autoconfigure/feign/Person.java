/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import com.ciicgat.grus.json.SimpleDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by August.Zhou on 2016/12/27 10:55.
 */
public class Person implements Serializable {

    private Integer id; // 主键ID

    private Integer enterpriseId; // 企业Id

    private Integer memberId; // 会员Id

    private Integer departmentId; // 部门Id

    private String code; // 员工号

    private Integer type; // 个人用户类型

    private String name; // 姓名

    private Integer gender; // 性别

    private Integer idCardType; // 证件类型

    private String idCardNo; // 证件号码

    private String idCardExpiryDate; // 证件有效期

    private Integer directorId; // 直接主管Id

    private Integer hrId; // 隶属人事Id

    private Integer isManager; // 是否部门主管

    private Integer isHr; // 是否人事管理员

    private String email; // 邮箱

    private String mobile; // 手机号

    private String phone; // 电话

    private String remark; // 备注

    private Integer birthyear; // 生日年

    private Integer birthmonth; // 生日月

    private Integer birthday; // 生日

    private Integer entryYear; // 入职年

    private Integer entryMonth; // 入职月

    private Integer entryDay; // 入职日

    private Integer memberLevel; // 员工级别

    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date timeActive; // 激活日期

    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date timeCreated; // 创建日期

    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date timeModified; // 修改日期

    private Integer isOpenBirthday; //

    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date nextBirthday; // 下次生日


    private String enterpriseName;

    private String departmentName;

    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date birthdate;    //生日

    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date entrydate;    //入职日期

    private Integer dimissionStatus;    //离职状态 1- 离职中  2-没离职

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(Integer idCardType) {
        this.idCardType = idCardType;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getIdCardExpiryDate() {
        return idCardExpiryDate;
    }

    public void setIdCardExpiryDate(String idCardExpiryDate) {
        this.idCardExpiryDate = idCardExpiryDate;
    }

    public Integer getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Integer directorId) {
        this.directorId = directorId;
    }

    public Integer getHrId() {
        return hrId;
    }

    public void setHrId(Integer hrId) {
        this.hrId = hrId;
    }

    public Integer getIsManager() {
        return isManager;
    }

    public void setIsManager(Integer isManager) {
        this.isManager = isManager;
    }

    public Integer getIsHr() {
        return isHr;
    }

    public void setIsHr(Integer isHr) {
        this.isHr = isHr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(Integer birthyear) {
        this.birthyear = birthyear;
    }

    public Integer getBirthmonth() {
        return birthmonth;
    }

    public void setBirthmonth(Integer birthmonth) {
        this.birthmonth = birthmonth;
    }

    public Integer getBirthday() {
        return birthday;
    }

    public void setBirthday(Integer birthday) {
        this.birthday = birthday;
    }

    public Integer getEntryYear() {
        return entryYear;
    }

    public void setEntryYear(Integer entryYear) {
        this.entryYear = entryYear;
    }

    public Integer getEntryMonth() {
        return entryMonth;
    }

    public void setEntryMonth(Integer entryMonth) {
        this.entryMonth = entryMonth;
    }

    public Integer getEntryDay() {
        return entryDay;
    }

    public void setEntryDay(Integer entryDay) {
        this.entryDay = entryDay;
    }

    public Integer getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(Integer memberLevel) {
        this.memberLevel = memberLevel;
    }

    public Date getTimeActive() {
        return timeActive;
    }

    public void setTimeActive(Date timeActive) {
        this.timeActive = timeActive;
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

    public Integer getIsOpenBirthday() {
        return isOpenBirthday;
    }

    public void setIsOpenBirthday(Integer isOpenBirthday) {
        this.isOpenBirthday = isOpenBirthday;
    }

    public Date getNextBirthday() {
        return nextBirthday;
    }

    public void setNextBirthday(Date nextBirthday) {
        this.nextBirthday = nextBirthday;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getDimissionStatus() {
        return dimissionStatus;
    }

    public void setDimissionStatus(Integer dimissionStatus) {
        this.dimissionStatus = dimissionStatus;
    }


}
