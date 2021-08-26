/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.form;


import feign.RequestTemplate;

import java.util.Map;

/**
 * Interface for form data processing.
 *
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 30.04.2016
 */
public interface FormDataProcessor {

    /**
     * Processing form data to request body.
     *
     * @param data     form data, where key is a parameter name and value is...a value.
     * @param template current request object.
     */
    void process(Map<String, Object> data, RequestTemplate template);

    /**
     * Returns {@code FormDataProcessor} implementation supporting Content-Type.
     *
     * @return supported MIME Content-Type
     */
    String getSupportetContentType();
}
