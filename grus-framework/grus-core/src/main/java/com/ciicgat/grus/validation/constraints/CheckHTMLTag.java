/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.validation.constraints;

import com.ciicgat.grus.validation.validators.CheckHTMLTagValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Stanley Shen
 * @version 1.0.0
 * @date 2019/8/12 10:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {CheckHTMLTagValidator.class})
public @interface CheckHTMLTag {

    String pattern() default "<script[^>]*?>.*?(</script>)?|<iframe[^>]*?>.*?(</iframe>)?|<[\\/\\!]*?[^<>]*?";


    String message() default "包含html";

    /**
     * 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        CheckHTMLTag[] value();
    }

}
