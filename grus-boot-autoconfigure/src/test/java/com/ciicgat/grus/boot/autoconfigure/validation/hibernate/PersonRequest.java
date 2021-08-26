/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.hibernate;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wanchongyang
 * @date 2020/5/9 11:06 下午
 */
public class PersonRequest {
    @NotNull
    private Integer personId;

    @NotBlank
    @Length(min = 5, max = 50)
    private String personName;

    @Valid
    private PersonPlusRequest personPlusRequest;

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public PersonPlusRequest getPersonPlusRequest() {
        return personPlusRequest;
    }

    public void setPersonPlusRequest(PersonPlusRequest personPlusRequest) {
        this.personPlusRequest = personPlusRequest;
    }
}
