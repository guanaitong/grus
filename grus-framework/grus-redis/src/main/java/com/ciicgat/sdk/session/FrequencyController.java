/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by August.Zhou on 2017/5/10 17:33.
 */
public interface FrequencyController {


    FrequencyController UNLIMIT = (httpServletRequest, httpServletResponse) -> false;
    FrequencyController FORBIDDEN = (httpServletRequest, httpServletResponse) -> true;

    boolean isOverLimit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);


}
