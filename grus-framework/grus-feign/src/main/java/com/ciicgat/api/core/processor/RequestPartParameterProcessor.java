/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.processor;

import feign.MethodMetadata;
import org.springframework.web.bind.annotation.RequestPart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import static feign.Util.checkState;
import static feign.Util.emptyToNull;

/**
 * {@link RequestPart} parameter processor.
 *
 * @author Aaron Whiteside
 * @see AnnotatedParameterProcessor
 */
public class RequestPartParameterProcessor implements AnnotatedParameterProcessor {

	private static final Class<RequestPart> ANNOTATION = RequestPart.class;

	@Override
	public Class<? extends Annotation> getAnnotationType() {
		return ANNOTATION;
	}

	@Override
	public boolean processArgument(AnnotatedParameterContext context,
			Annotation annotation, Method method) {
		int parameterIndex = context.getParameterIndex();
		MethodMetadata data = context.getMethodMetadata();

		String name = ANNOTATION.cast(annotation).value();
		checkState(emptyToNull(name) != null,
				"RequestPart.value() was empty on parameter %s", parameterIndex);
		context.setParameterName(name);

		data.formParams().add(name);
		Collection<String> names = context.setTemplateParameter(name,
				data.indexToName().get(parameterIndex));
		data.indexToName().put(parameterIndex, names);
		return true;
	}

}
