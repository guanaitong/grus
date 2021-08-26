/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class TestModel {

    private String name;

    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal balance;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date time;

    private Date time2;

    @JsonDeserialize(using = SimpleDateDeserializer.class)
    private Date time3;

    private Date time4;

    private Date time5;

    private Date time6;

    @JsonDeserialize(using = RateDeserializer.class)
    private BigDecimal rate;

    @JsonDeserialize(using = DebeziumDecimalDeserializer.class)
    private BigDecimal weight;

    @JsonDeserialize(using = DebeziumDecimalDeserializer.class)
    private BigDecimal score;

    @JsonDeserialize(using = GMTPlus8Deserializer.class)
    private Date timeCreated;

    public Date getTime2() {
        return time2;
    }

    public void setTime2(Date time2) {
        this.time2 = time2;
    }

    public Date getTime3() {
        return time3;
    }

    public void setTime3(Date time3) {
        this.time3 = time3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime4() {
        return time4;
    }

    public void setTime4(Date time4) {
        this.time4 = time4;
    }

    public Date getTime5() {
        return time5;
    }

    public void setTime5(Date time5) {
        this.time5 = time5;
    }

    public Date getTime6() {
        return time6;
    }

    public void setTime6(Date time6) {
        this.time6 = time6;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestModel)) return false;
        TestModel testModel = (TestModel) o;
        return Objects.equals(name, testModel.name) &&
                Objects.equals(balance, testModel.balance) &&
                Objects.equals(time, testModel.time) &&
                Objects.equals(time2, testModel.time2) &&
                Objects.equals(rate, testModel.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, balance, time, time2, rate);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
