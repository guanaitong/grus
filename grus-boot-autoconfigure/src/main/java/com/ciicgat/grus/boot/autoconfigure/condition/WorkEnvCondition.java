/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.condition;

import com.ciicgat.sdk.util.system.WorkEnv;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * Created by August.Zhou on 2019-04-18 11:08.
 */
public class WorkEnvCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (System.getenv("RUN_ENV") != null) {
            return ConditionOutcome.match();
        }

        WorkEnv currentWorkEnv = WorkRegion.getCurrentWorkRegion().getWorkEnv();
        MultiValueMap<String, Object> annotationAttributes = metadata.getAllAnnotationAttributes(ConditionalOnWorkEnv.class.getName());
        if (annotationAttributes == null || annotationAttributes.isEmpty()) {
            return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnWorkEnv.class).notAvailable("项目未使用@ConditionalOnWorkEnv"));
        }
        List<Object> value = annotationAttributes.get("value");
        WorkEnv[] workEnvs = (WorkEnv[]) value.get(0);
        for (WorkEnv workEnv : workEnvs) {
            if (currentWorkEnv == workEnv) {
                return ConditionOutcome.match();
            }
        }

        return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnWorkEnv.class).notAvailable("在当前环境" + currentWorkEnv + "不可用"));
    }
}
