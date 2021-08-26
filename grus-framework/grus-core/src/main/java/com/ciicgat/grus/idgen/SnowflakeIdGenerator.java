/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.idgen;

import com.ciicgat.sdk.util.concurrent.LoopAtomicLong;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/18 15:39
 * @Description: 基于雪花算法的序列生成器，字符串类型，序列长度24位
 */
public class SnowflakeIdGenerator implements IdGenerator {

    private final int workIdBits = 10;
    private final int secondsBits = 17;
    private final int sequenceBits = 17;
    private final int randomBits = 8;

    private final int maxWorkId = 1 << workIdBits;
    private final int maxSequence = 1 << sequenceBits;
    private final int maxRandom = 1 << randomBits;
    private final LocalDateTime baseDate = LocalDateTime.of(2020, 1, 1, 0, 0);

    private final LoopAtomicLong loopAtomicNo = new LoopAtomicLong(maxSequence);
    private final LoopAtomicLong loopAtomicId = new LoopAtomicLong(maxSequence);

    private WorkIdHolder workIdHolder;
    private DateTimeFormatter dateTimeFormatter;

    public SnowflakeIdGenerator(WorkIdHolder workIdHolder, String dateFormat) {
        this.workIdHolder = workIdHolder;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    public String makeNo() {
        LocalDateTime time = LocalDateTime.now();
        long workId = workIdHolder.getId(maxWorkId);
        long seconds = time.getHour() * 3600L + time.getMinute() * 60L + time.getSecond();
        long random = ThreadLocalRandom.current().nextInt(maxRandom);
        long sequence = loopAtomicNo.loopGet();

        // 机器码 10位 + 天秒数 17位 + 循环自增序列 17bit + 随机数 8bit = 52bit   16位整数
        long id = (workId << (secondsBits + sequenceBits + randomBits))
                | (seconds << (sequenceBits + randomBits))
                | (sequence << randomBits)
                | random;

        String resultNo = time.format(dateTimeFormatter);
        resultNo += StringUtils.leftPad(String.valueOf(id), 16, "0");

        return resultNo;
    }

    @Override
    public long makeId() {
        LocalDateTime time = LocalDateTime.now();
        long workId = workIdHolder.getId(maxWorkId);
        long days = Duration.between(baseDate, time).toDays();
        long seconds = time.getHour() * 3600L + time.getMinute() * 60L + time.getSecond();
        long sequence = loopAtomicId.loopGet();

        // 天数 20位 + 天秒数 17位 + 机器码 10位 + 循环自增序列 17bit = 64bit
        long id = (days << (secondsBits + workIdBits + sequenceBits))
                | (seconds << (workIdBits + sequenceBits))
                | (workId << sequenceBits)
                | sequence;
        return id;
    }
}
