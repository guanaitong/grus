/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.processor;

import feign.MethodMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static feign.Util.checkState;
import static feign.Util.emptyToNull;

/**
 * {@link PathVariable} parameter processor.
 *
 * @author Jakub Narloch
 * @author Abhijit Sarkar
 * @see AnnotatedParameterProcessor
 */
public class PathVariableParameterProcessor implements AnnotatedParameterProcessor {

	private static final Class<PathVariable> ANNOTATION = PathVariable.class;

	@Override
	public Class<? extends Annotation> getAnnotationType() {
		return ANNOTATION;
	}

	@Override
	public boolean processArgument(AnnotatedParameterContext context,
			Annotation annotation, Method method) {
		String name = ANNOTATION.cast(annotation).value();
		checkState(emptyToNull(name) != null,
				"PathVariable annotation was empty on param %s.",
				context.getParameterIndex());
		context.setParameterName(name);

		MethodMetadata data = context.getMethodMetadata();
		String varName = '{' + name + '}';
		if (!data.template().url().contains(varName)
				&& !searchMapValues(data.template().queries(), varName)
				&& !searchMapValues(data.template().headers(), varName)) {
			data.formParams().add(name);
		}
		return true;
	}

	private <K, V> boolean searchMapValues(Map<K, Collection<V>> map, V search) {
		Collection<Collection<V>> values = map.values();
		if (CollectionUtils.isEmpty(values)) {
			return false;
		}
		for (Collection<V> entry : values) {
			if (Objects.nonNull(entry) && entry.contains(search)) {
				return true;
			}
		}
		return false;
	}

}
