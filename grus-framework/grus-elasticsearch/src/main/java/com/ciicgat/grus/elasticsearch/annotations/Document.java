/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.annotations;

import com.ciicgat.grus.elasticsearch.core.IndexSuffixType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by August.Zhou on 2019-09-09 12:27.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Document {

    /**
     * 索引的名字
     *
     * @return
     */
    String index();

    /**
     * 索引的后缀生成规则
     *
     * @return
     */
    IndexSuffixType indexSuffixType() default IndexSuffixType.NONE;

    /**
     * 索引的别名，查询的时候使用到
     *
     * @return
     */
    String alias();

    /**
     * mapping.json文件的存储路径
     *
     * @return
     */
    String mappingPath();

    /**
     * setting.json文件的存储路径。如果设置了该值，那么shards和replicas的设置不生效
     *
     * @return
     */
    String settingPath() default "";

    /**
     * 分片数
     *
     * @return
     */
    short shards() default 3;

    /**
     * 副本数
     *
     * @return
     */
    short replicas() default 1;

    /**
     * 查询的分页最大条数
     * @return
     */
    int max_result_window() default 0;

}
