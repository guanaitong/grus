/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.condition;

import com.ciicgat.grus.boot.autoconfigure.constants.GrusConstants;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by August.Zhou on 2020-04-22 9:47.
 */
public class GconfConfigKeyCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> annotationAttributes = metadata.getAllAnnotationAttributes(
                ConditionalOnGconfConfigKey.class.getName());
        List<Object> value = annotationAttributes.get("value");
        String key = (String) value.get(0);
        // 这边Systems.AppName可能未加载，所以优先从context env里取
        String appName = context.getEnvironment().getProperty(GrusConstants.APP_NAME_KEY);
        ConfigCollection configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection(appName);
        if (configCollection == null || !StringUtils.hasLength(configCollection.getConfig(key))) {
            return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnGconfConfigKey.class).notAvailable(key + "在gconf上不存在"));
        }
        return ConditionOutcome.match();
    }
}
