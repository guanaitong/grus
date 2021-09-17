/*
 * Copyright 2007-${.now?string('yyyy')}, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package ${mapperImplPackage};

import ${entityPackage}.${entity.entityName};
import ${mapperPackage}.${entity.upperCamelName}${mapperSuffix};
import com.ciicgat.sdk.data.mybatis.generator.template.ReadWriteSeparationMapperImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class ${entity.fileName} extends ReadWriteSeparationMapperImpl<${entity.entityName}> implements ${entity.upperCamelName}${mapperSuffix} {

}
