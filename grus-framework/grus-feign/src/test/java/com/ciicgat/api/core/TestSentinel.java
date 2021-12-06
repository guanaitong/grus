/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.ciicgat.api.core.service.SentinelService;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSentinel {

    private static MockWebServer mockWebServer;
    private static SentinelService sentinelService;

    @Test
    public void testFlow() throws IOException {
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("1")
                .setResponseCode(200);
        initFlowRule();
        mockWebServer.enqueue(mockResponse);
        Assertions.assertThrows(BusinessRuntimeException.class, () -> sentinelService.testFlow());
        mockWebServer.shutdown();
    }

    @Test
    public void testDegrade() throws IOException {
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        initDegradeRule();
        for (int i = 0; i < 10; i++) {
            try {
                sentinelService.testDegrade();
            } catch (Exception e) {

            }
        }
        Assertions.assertThrows(BusinessRuntimeException.class, () -> sentinelService.testDegrade());
        mockWebServer.shutdown();
    }

    @Test
    public void testAuthority() throws IOException {
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("4")
                .setResponseCode(200);
        initAuthorityRule();
        mockWebServer.enqueue(mockResponse);
        Assertions.assertThrows(BusinessRuntimeException.class, () -> sentinelService.testAuthority());
        mockWebServer.shutdown();
    }

    @Test
    public void testParamFlow() throws IOException {
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("5")
                .setResponseCode(200);
        initParamFlowRule();
        mockWebServer.enqueue(mockResponse);
        sentinelService.testParamFlow(5, 5);
        Assertions.assertThrows(BusinessRuntimeException.class, () -> sentinelService.testParamFlow(5, 5));
        mockWebServer.shutdown();
    }

    @Test
    public void testFlowFallback() throws IOException {
        TestSentinelFallback fallback = new TestSentinelFallback();
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true, fallback);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("1")
                .setResponseCode(200);
        initFlowRule();
        mockWebServer.enqueue(mockResponse);
        int i = sentinelService.testFlow();
        Assertions.assertEquals(i, 1);
        mockWebServer.shutdown();
    }

    @Test
    public void testDegradeFallback() throws IOException {
        TestSentinelFallback fallback = new TestSentinelFallback();
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true, fallback);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        initDegradeRule();
        for (int i = 0; i < 10; i++) {
            try {
                sentinelService.testDegrade();
            } catch (Exception e) {

            }
        }
        int i = sentinelService.testDegrade();
        Assertions.assertEquals(i, 2);
        mockWebServer.shutdown();
    }

    @Test
    public void testAuthorityFallback() throws IOException {
        TestSentinelFallback fallback = new TestSentinelFallback();
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true, fallback);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("4")
                .setResponseCode(200);
        initAuthorityRule();
        mockWebServer.enqueue(mockResponse);
        int i = sentinelService.testAuthority();
        Assertions.assertEquals(i, 3);
        mockWebServer.shutdown();
    }

    @Test
    public void testParamFlowFallback() throws IOException {
        TestSentinelFallback fallback = new TestSentinelFallback();
        Pair<SentinelService, MockWebServer> pair = TestUtil.newInstance("sentinel-test", SentinelService.class, true, fallback);
        mockWebServer = pair.getRight();
        sentinelService = pair.getLeft();
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("5")
                .setResponseCode(200);
        initParamFlowRule();
        mockWebServer.enqueue(mockResponse);
        sentinelService.testParamFlow(5, 5);
        int i = sentinelService.testParamFlow(5, 5);
        Assertions.assertEquals(i, 4);
        mockWebServer.shutdown();
    }

    private void initFlowRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("POST:sentinel-test/testFlow");
        rule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        rule.setCount(0);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    private void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("POST:sentinel-test/testDegrade");
        rule.setCount(0);
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        rule.setTimeWindow(1);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    private void initAuthorityRule() {
        AuthorityRule rule = new AuthorityRule();
        rule.setResource("POST:sentinel-test/testAuthority");
        rule.setStrategy(RuleConstant.AUTHORITY_BLACK);
        rule.setLimitApp("grus-demo");
        AuthorityRuleManager.loadRules(Collections.singletonList(rule));
    }

    private void initParamFlowRule() {
        ParamFlowRule rule = new ParamFlowRule("POST:sentinel-test/testParamFlow")
                .setParamIdx(0)
                .setCount(1);
        // 针对 int 类型的参数 PARAM_B，单独设置限流 QPS 阈值为 10，而不是全局的阈值 5.
        ParamFlowItem item = new ParamFlowItem().setObject("id")
                .setClassType(int.class.getName())
                .setCount(1);
        rule.setParamFlowItemList(Collections.singletonList(item));
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }

}
