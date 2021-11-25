/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.springcache.event.extend;

import com.ciicgat.sdk.springcache.event.CacheChangeEvent;
import com.ciicgat.sdk.springcache.event.CacheChangeListener;
import com.ciicgat.sdk.springcache.event.CacheChangeType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by August.Zhou on 2021/11/26 13:18.
 */
public class ClearAfterHttpCompletionCacheChangeListener implements CacheChangeListener {
    private static final String CACHE_KEY = "__CHANGE_CACHE_KEY__";

    public ClearAfterHttpCompletionCacheChangeListener() {
    }

    @Override
    public void onChanged(CacheChangeEvent cacheChangeEvent) {
        /**
         * 只处理evict，put直接放回
         */
        if (cacheChangeEvent.getCacheChangeType() == CacheChangeType.PUT) {
            return;
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        List<CacheChangeEvent> cacheChangeEvents = (List<CacheChangeEvent>) httpServletRequest.getAttribute(CACHE_KEY);
        if (cacheChangeEvents == null) {
            cacheChangeEvents = new ArrayList<>(8);
            httpServletRequest.setAttribute(CACHE_KEY, cacheChangeEvents);
        }
        cacheChangeEvents.add(cacheChangeEvent);
    }

    /**
     * call  afterCompletion
     */
    public void clear() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        List<CacheChangeEvent> cacheChangeEvents = (List<CacheChangeEvent>) httpServletRequest.getAttribute(CACHE_KEY);
        if (cacheChangeEvents == null || cacheChangeEvents.size() == 0) {
            return;
        }
        /**
         * 拷贝出一个新的list，防止evict的时候死循环
         */
        CacheChangeEvent[] cacheChangeEventsCopy = cacheChangeEvents.toArray(new CacheChangeEvent[cacheChangeEvents.size()]);
        for (CacheChangeEvent cacheChangeEvent : cacheChangeEventsCopy) {
            cacheChangeEvent.getCache().evict(cacheChangeEvent.getKey());
        }
    }
}
