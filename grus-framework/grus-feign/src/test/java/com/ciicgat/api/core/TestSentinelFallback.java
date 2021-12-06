package com.ciicgat.api.core;

import com.ciicgat.api.core.service.SentinelService;

public class TestSentinelFallback implements FallbackFactory<SentinelService> {
    @Override
    public SentinelService create(Throwable cause) {
        SentinelService sentinelService = new SentinelService() {
            @Override
            public int testFlow() {
                return 1;
            }

            @Override
            public int testDegrade() {
                return 2;
            }

            @Override
            public int testAuthority() {
                return 3;
            }

            @Override
            public int testParamFlow(Integer personId, Integer id) {
                return 4;
            }
        };
        return sentinelService;
    }
}
