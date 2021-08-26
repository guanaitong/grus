/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import feign.FeignException;

/**
 * Used to control the fallback given its cause.
 * <p>
 * Ex.
 *
 * <pre>
 * {@code
 * // This instance will be invoked if there are errors of any kind.
 * FallbackFactory<GitHub> fallbackFactory = cause -> (owner, repo) -> {
 *   if (cause instanceof FeignException && ((FeignException) cause).status() == 403) {
 *     return Collections.emptyList();
 *   } else {
 *     return Arrays.asList("yogi");
 *   }
 * };
 *
 * GitHub github = HystrixFeign.builder()
 *                             ...
 *                             .target(GitHub.class, "https://api.github.com", fallbackFactory);
 * }
 * </pre>
 *
 * @param <T> the feign interface type
 */
public interface FallbackFactory<T> {

    /**
     * Returns an instance of the fallback appropriate for the given cause
     *
     * @param cause often an instance of {@link FeignException}.
     */
    T create(Throwable cause);

    /**
     * Returns a constant fallback after logging the cause to FINE level.
     */
    final class Default<T> implements FallbackFactory<T> {
        final T constant;

        public Default(T constant) {
            this.constant = constant;
        }

        @Override
        public T create(Throwable cause) {
            return constant;
        }

        @Override
        public String toString() {
            return constant.toString();
        }
    }
}
