/*
 * Copyright 2007-${.now?string('yyyy')}, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package ${serviceImplPackage};

import com.ciicgat.sdk.data.mybatis.generator.template.BaseServiceImpl;
import ${entityPackage}.${entity.entityName};
import ${servicePackage}.${entity.upperCamelName}${serviceSuffix};
import org.springframework.stereotype.Service;

/**
 * ${entity.comment} 服务实现
 */
@Service
public class ${entity.fileName} extends BaseServiceImpl<${entity.entityName}> implements ${entity.upperCamelName}${serviceSuffix} {

}
