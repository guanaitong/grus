/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.condition;

import com.ciicgat.grus.boot.autoconfigure.constants.GrusConstants;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by August.Zhou on 2019-04-18 11:08.
 */
public class ServerEnvCondition extends SpringBootCondition {

    @VisibleForTesting
    public static boolean isTest = false; //NOSONAR

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (isTest) {
            return ConditionOutcome.match();
        }
        if (System.getenv("KUBERNETES_SERVICE_HOST") != null) {
            return ConditionOutcome.match();
        }
        if (System.getenv(GrusConstants.WORK_ENV) != null && System.getenv(GrusConstants.WORK_IDC) != null) {
            return ConditionOutcome.match();
        }

        return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnServerEnv.class).notAvailable("在本地环境不可用"));
    }
}
