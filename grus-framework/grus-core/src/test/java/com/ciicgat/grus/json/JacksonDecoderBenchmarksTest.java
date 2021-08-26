/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2018/9/19 10:30.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JacksonDecoderBenchmarksTest {

    private final ObjectMapper objectMapper;

    private final String json = "{\n" +
            "  \"code\": 0,\n" +
            "  \"msg\": \"OK\",\n" +
            "  \"data\": {\n" +
            "    \"id\": 1,\n" +
            "    \"enterpriseId\": 1,\n" +
            "    \"memberId\": 3,\n" +
            "    \"departmentId\": 42094,\n" +
            "    \"code\": \"fkh47c77mz\",\n" +
            "    \"type\": 1,\n" +
            "    \"name\": \"123\",\n" +
            "    \"gender\": 2,\n" +
            "    \"idCardType\": 1,\n" +
            "    \"idCardNo\": \"412725199001021323\",\n" +
            "    \"idCardExpiryDate\": \"\",\n" +
            "    \"directorId\": 1,\n" +
            "    \"hrId\": 1,\n" +
            "    \"isManager\": 2,\n" +
            "    \"isHr\": 2,\n" +
            "    \"email\": \"44jctn@ka.cn\",\n" +
            "    \"mobile\": \"13971617312\",\n" +
            "    \"phone\": \"15896982\",\n" +
            "    \"remark\": \"txk9p6\",\n" +
            "    \"birthyear\": 2017,\n" +
            "    \"birthmonth\": 10,\n" +
            "    \"birthday\": 9,\n" +
            "    \"entryYear\": 0,\n" +
            "    \"entryMonth\": 1,\n" +
            "    \"entryDay\": 15,\n" +
            "    \"memberLevel\": 1,\n" +
            "    \"timeActive\": \"2009-04-29 18:07:58\",\n" +
            "    \"timeCreated\": \"2009-03-19 16:13:56\",\n" +
            "    \"timeModified\": \"2018-06-13 10:48:52\",\n" +
            "    \"isOpenBirthday\": 1,\n" +
            "    \"nextBirthday\": \"2018-10-09\",\n" +
            "    \"aiCardCode\": \"\"\n" +
            "  }\n" +
            "}";

    private final JavaType javaType;

    public JacksonDecoderBenchmarksTest() {
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        javaType = objectMapper.constructType(Person.class);
    }

    @Benchmark//对要被测试性能的代码添加注解，说明该方法是要被测试性能的
    public void decodeByConvertValue() throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);
        Object o = objectMapper.convertValue(rootNode, javaType);
    }

    @Benchmark//对要被测试性能的代码添加注解，说明该方法是要被测试性能的
    public void decodeByReadTree() throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonParser jsonParser = objectMapper.treeAsTokens(rootNode);
        Object o = objectMapper.readValue(jsonParser, javaType);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JacksonDecoderBenchmarksTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(1000)
                .warmupTime(TimeValue.milliseconds(1))
                .measurementTime(TimeValue.milliseconds(1))
                .measurementIterations(5000)
                .build();

        new Runner(opt).run();
    }


}
