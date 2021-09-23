/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.TestBean;
import feign.Param;
import feign.RequestLine;

/**
 * Created by August.Zhou on 2021/9/23 16:54.
 */
@ServiceName("default-method")
public interface DefaultMethodService {

    default TestBean get1(@Param("serverId") String serverId, @Param("count") int count) {
        return get2(serverId, count);
    }


    @RequestLine("GET /get/{serverId}?count={count}")
    TestBean get2(@Param("serverId") String serverId, @Param("count") int count);
}
