/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.form;

import com.ciicgat.api.core.annotation.UrlFormBody;
import feign.RequestTemplate;
import feign.codec.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Properly encodes requests with <b>application/x-www-form-urlencoded</b> and <b>multipart/form-data</b> Content-Type.
 * <p>
 * Also, the encoder has a <b>delegate</b> field for encoding non-form requests (like JSON or other).
 * <p>
 * Default <b>delegate</b> object is {@link feign.codec.Encoder.Default} instance.
 * <p>
 * Usage example:
 * <p>
 * <b>Declaring API interface:</b>
 * <pre>
 * interface SomeApi {
 *
 *     &#064;RequestLine("POST /json")
 *     &#064;Headers("Content-Type: application/json")
 *     void json (Dto dto);
 *
 *     &#064;RequestLine("POST /form")
 *     &#064;Headers("Content-Type: application/x-www-form-urlencoded")
 *     void from (@Param("field1") String field1, @Param("field2") String field2);
 *
 * }
 * </pre>
 * <p>
 * <b>Creating Feign client instance:</b>
 * <pre>
 * SomeApi service = Feign.builder()
 *       .encoder(new FormEncoder(new JacksonEncoder()))
 *       .target(SomeApi.class, "http://localhost:8080");
 * </pre>
 * <p>
 * Now it can handle JSON Content-Type by {@code feign.jackson.JacksonEncoder} and
 * form request by {@link FormEncoder}.
 *
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 30.04.2016
 */
@SuppressWarnings("unchecked")
public class FormEncoder implements Encoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormEncoder.class);
    private static ConcurrentMap<Class<?>, List<PropertyDescriptor>> beanInfos = new ConcurrentHashMap<>();
    private final Encoder deligate;
    private final Map<String, FormDataProcessor> processors;

    /**
     * Default {@code FormEncoder} constructor.
     * <p>
     * Sets {@link feign.codec.Encoder.Default} instance as delegate encoder.
     */
    public FormEncoder() {
        this(new Encoder.Default());
    }

    /**
     * {@code FormEncoder} constructor with delegate encoder argument.
     * <p>
     *
     * @param delegate delegate encoder for processing non-form requests.
     */
    public FormEncoder(Encoder delegate) {
        this.deligate = delegate;
        processors = new HashMap<>(2, 1.F);

        FormEncodedDataProcessor formEncodedDataProcessor = new FormEncodedDataProcessor();
        processors.put(formEncodedDataProcessor.getSupportetContentType().toLowerCase(),
                formEncodedDataProcessor);

        MultipartEncodedDataProcessor multipartEncodedDataProcessor = new MultipartEncodedDataProcessor();
        processors.put(multipartEncodedDataProcessor.getSupportetContentType().toLowerCase(),
                multipartEncodedDataProcessor);
    }

    private static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null)
            return null;

        Map<String, Object> map = new HashMap<>();

        for (PropertyDescriptor property : getLists(obj.getClass())) {
            String key = property.getName();
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            if (value != null) {
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    StringJoiner joiner = new StringJoiner(",");
                    for (Object cs : collection) {
                        joiner.add(cs.toString());
                    }
                    map.put(key, joiner.toString());
                } else {
                    map.put(key, value);
                }
            }

        }

        return map;
    }

    private static List<PropertyDescriptor> getLists(Class<?> clazz) {
        List<PropertyDescriptor> propertyDescriptors = beanInfos.get(clazz);
        if (propertyDescriptors == null) {
            try {
                propertyDescriptors = new ArrayList<>();
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                    String key = property.getName();
                    if (key.compareToIgnoreCase("class") == 0) {
                        continue;
                    }
                    propertyDescriptors.add(property);
                }
                beanInfos.put(clazz, propertyDescriptors);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return propertyDescriptors;

    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {
        String formType = "";
        for (Map.Entry<String, Collection<String>> entry : template.headers().entrySet()) {
            if (!entry.getKey().equalsIgnoreCase("Content-Type")) {
                continue;
            }
            for (String contentType : entry.getValue()) {
                if (contentType != null && processors.containsKey(contentType.toLowerCase())) {
                    formType = contentType;
                    break;
                }
            }
            if (!formType.isEmpty()) {
                break;
            }
        }

        if (formType.isEmpty()) {
            deligate.encode(object, bodyType, template);
            return;
        }

        if (!MAP_STRING_WILDCARD.equals(bodyType)) {
            if (bodyType instanceof Class) {
                Class clazz = (Class) bodyType;
                if (clazz.getAnnotation(UrlFormBody.class) != null) {
                    try {
                        Map<String, Object> beanMap = objectToMap(object);
                        processors.get(formType).process(beanMap, template);
                    } catch (Exception e) {
                        LOGGER.error("convert object to map failed", e);
                    }
                    return;
                }
            }

            deligate.encode(object, bodyType, template);
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) object;
        processors.get(formType).process(data, template);
    }

}
