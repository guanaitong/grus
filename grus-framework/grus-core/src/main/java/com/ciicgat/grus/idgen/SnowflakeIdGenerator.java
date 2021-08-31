/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
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
    private static final int WORK_ID_BITS = 10;
    private static final int SECONDS_BITS = 17;
    private static final int SEQUENCE_BITS = 17;
    private static final int RANDOM_BITS = 8;

    private static final int MAX_WORK_ID = 1 << WORK_ID_BITS;
    private static final int MAX_SEQUENCE = 1 << SEQUENCE_BITS;
    private static final int MAX_RANDOM = 1 << RANDOM_BITS;
    private static final LocalDateTime BASE_DATE = LocalDateTime.of(2020, 1, 1, 0, 0);

    private final LoopAtomicLong loopAtomicNo = new LoopAtomicLong(MAX_SEQUENCE);
    private final LoopAtomicLong loopAtomicId = new LoopAtomicLong(MAX_SEQUENCE);

    private DateTimeFormatter dateTimeFormatter;
    private long workId;

    public SnowflakeIdGenerator(WorkIdHolder workIdHolder, String dateFormat) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        workId = workIdHolder.getId(MAX_WORK_ID);
    }

    @Override
    public String makeNo() {
        LocalDateTime time = LocalDateTime.now();

        long seconds = time.getHour() * 3600L + time.getMinute() * 60L + time.getSecond();
        long random = ThreadLocalRandom.current().nextInt(MAX_RANDOM);
        long sequence = loopAtomicNo.loopGet();

        // 机器码 10位 + 天秒数 17位 + 循环自增序列 17bit + 随机数 8bit = 52bit   16位整数
        long id = (workId << (SECONDS_BITS + SEQUENCE_BITS + RANDOM_BITS))
                | (seconds << (SEQUENCE_BITS + RANDOM_BITS))
                | (sequence << RANDOM_BITS)
                | random;

        String resultNo = time.format(dateTimeFormatter);
        resultNo += StringUtils.leftPad(String.valueOf(id), 16, "0");

        return resultNo;
    }

    @Override
    public long makeId() {
        LocalDateTime time = LocalDateTime.now();
        long days = Duration.between(BASE_DATE, time).toDays();
        long seconds = time.getHour() * 3600L + time.getMinute() * 60L + time.getSecond();
        long sequence = loopAtomicId.loopGet();

        // 天数 20位 + 天秒数 17位 + 机器码 10位 + 循环自增序列 17bit = 64bit
        long id = (days << (SECONDS_BITS + WORK_ID_BITS + SEQUENCE_BITS))
                | (seconds << (WORK_ID_BITS + SEQUENCE_BITS))
                | (workId << SEQUENCE_BITS)
                | sequence;
        return id;
    }
}
