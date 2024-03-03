/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;

/**
 * @author August
 * @date 3/15/2024 5:40 PM
 */
public interface RuleOptions<T extends AbstractRule> {

    T toSentinelRule(String resource);
}
