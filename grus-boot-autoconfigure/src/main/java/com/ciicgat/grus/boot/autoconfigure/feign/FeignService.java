/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import com.ciicgat.api.core.FallbackFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to create a grus reference and set it to the annotated field of a Spring bean. Sample usage:
 *
 * <pre>
 *
 * public class ReferenceAnnotationSample {
 *
 *     &#064;FeignService
 *     private SampleService sampleService;
 * }
 * </pre>
 * Created by August.Zhou on 2019-03-04 18:51.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@SuppressWarnings("null")
public @interface FeignService {

    /**
     * The type of the grus reference to be created. Default to the type of field annotated when not specified.
     *
     * @return return interface type
     */
    Class<?> interfaceType() default void.class;


    /**
     * binding of reference
     *
     * @return binding of reference
     */
    TimeoutBinding timeoutBinding() default @TimeoutBinding;

    /**
     * binding of reference
     *
     * @return binding of reference
     */
    CacheBinding cacheBinding() default @CacheBinding;

    /**
     * Fallback class for the specified Feign client interface. The fallback class must
     * implement the interface annotated by this annotation and be a valid spring bean.
     *
     * @return fallback class for the specified Feign client interface
     */
    Class<?> fallback() default void.class;

    /**
     * Define a fallback factory for the specified Feign client interface. The fallback
     * factory must produce instances of fallback classes that implement the interface
     * annotated by {@link FeignService}. The fallback factory must be a valid spring bean.
     *
     * @return fallback factory for the specified Feign client interface
     * @see com.ciicgat.api.core.FallbackFactory for details.
     */
    Class<? extends FallbackFactory> fallbackFactory() default FallbackFactory.class;
}
