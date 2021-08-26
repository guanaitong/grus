/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import com.ciicgat.grus.elasticsearch.annotations.Field;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by August.Zhou on 2019-09-11 12:59.
 */
public class ElasticsearchModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public ElasticsearchModule() {
        super();
        setSerializerModifier(new ElasticsearchSerializerModifier());
        addSerializer(Date.class, new DateSerializer());
        addDeserializer(Date.class, new DateDeserializer());
    }

    private class ElasticsearchSerializerModifier extends BeanSerializerModifier {
        ElasticsearchSerializerModifier() {
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            List<BeanPropertyWriter> result = new ArrayList<>(beanProperties.size());

            for (BeanPropertyWriter beanPropertyWriter : beanProperties) {
                //序列化的时候，忽略docId和index两个字段
                if ("docId".equals(beanPropertyWriter.getName())
                        || "docIndex".equals(beanPropertyWriter.getName())
                        || "timestampSupplier".equals(beanPropertyWriter.getName())
                        || "pipelineSupplier".equals(beanPropertyWriter.getName())) {
                    continue;
                }
                Field annotation = beanPropertyWriter.getAnnotation(Field.class);
                if (annotation != null && annotation.ignore()) {
                    continue;
                }
                result.add(beanPropertyWriter);
            }

            return result;
        }
    }

    private static String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    private static Locale locale = Locale.forLanguageTag("zh");

    class DateSerializer extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            String format = DateFormatUtils.format(value, pattern, timeZone, locale);
            gen.writeObject(format);
        }
    }

    class DateDeserializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser jp, DeserializationContext context) throws IOException {
            String value = jp.getValueAsString();
            try {
                return DateUtils.parseDate(value, locale, pattern);
            } catch (ParseException e) {
                throw new EsDataException(e);
            }
        }
    }
}
