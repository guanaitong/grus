/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

/**
 * use ConfigCollectionFactoryBuilder to build it
 * Created by August.Zhou on 2016/10/11 10:59.
 */
public interface ConfigCollectionFactory {


    /**
     * 根据id，获取其下的配置集合。如果不存在，返回null
     *
     * @param configCollectionId
     * @return
     */
    ConfigCollection getConfigCollection(String configCollectionId);


    /**
     * 获取当前应用的gconf配置
     *
     * @return
     */
    ConfigCollection getConfigCollection();

}
