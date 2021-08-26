/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.constants;

/**
 * Created by August.Zhou on 2019-02-22 10:32.
 */
public interface GrusConstants {

    /***
     * 获取应用名: 备注 @Value("${spring.application.name:@null}")
     */
    String APP_NAME_KEY = "spring.application.name";


    String GRUS_RUNTIME_CONTEXT_BEAN_ID = "grusRuntimeContext";

    String GRUS_RUNTIME_MANAGER_BEAN_ID = "grusRuntimeManager";

    String WORK_ENV = "WORK_ENV";

    String WORK_IDC = "WORK_IDC";
}
