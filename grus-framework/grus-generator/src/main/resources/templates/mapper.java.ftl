/*
 * Copyright 2007-${.now?string('yyyy')}, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package ${mapperPackage};

import ${entityPackage}.${entity.entityName};
import com.ciicgat.sdk.data.mybatis.generator.template.BaseMapper;[#if baseConfig.enableMapperAnnotation]
import org.apache.ibatis.annotations.Mapper;[/#if]

/**
 * ${entity.comment} Mapper
 */[#if baseConfig.enableMapperAnnotation]
@Mapper[/#if]
public interface ${entity.fileName} extends BaseMapper<${entity.entityName}> {

}
