/*
 * Copyright 2007-${.now?string('yyyy')}, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package ${servicePackage};

import com.ciicgat.sdk.data.mybatis.generator.template.BaseService;
import ${entityPackage}.${entity.entityName};

/**
 * ${entity.comment} 服务接口
 */
public interface ${entity.fileName} extends BaseService<${entity.entityName}> {

}
