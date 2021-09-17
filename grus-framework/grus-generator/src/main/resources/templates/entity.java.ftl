/*
 * Copyright 2007-${.now?string('yyyy')}, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package ${entityPackage};

import com.ciicgat.sdk.data.mybatis.generator.annotation.TableField;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableId;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableName;

import java.io.Serializable;
[#if entity.hasBigDecimalField]
import java.math.BigDecimal;
[/#if]
import java.util.Date;
[#if baseConfig.enableLombok]

import lombok.Data;
[/#if]

/**
 * ${entity.comment}
 */
[#if baseConfig.enableLombok]
@Data
[/#if]
@TableName("${entity.tableName}")
public class ${entity.entityName} implements Serializable {

[#--fields--]
    private static final long serialVersionUID = 1L;

[#list entity.fields as field]
    /**
     * ${field.comment}
     */
    [#if field.markColumnName || field.ignoreSaving]
        [#if field.markColumnName && !field.ignoreSaving]
    @TableField("${field.columnName}")
        [#else]
    @TableField([#if field.markColumnName]value = "${field.columnName}"[/#if][#if field.markColumnName && field.ignoreSaving], [/#if][#if field.ignoreSaving]ignoreSaving = true[/#if])
        [/#if]
    [/#if]
    [#if field.primaryKey]
    @TableId
    [/#if]
    private ${field.jdbcType.javaType.simpleName} ${field.lowerCamelName};

[/#list]
[#-- getters & setters --]
[#if !baseConfig.enableLombok]
[#list entity.fields as field]
    public ${field.jdbcType.javaType.simpleName} get${field.upperCamelName}() {
        return ${field.lowerCamelName};
    }

    public void set${field.upperCamelName}(${field.jdbcType.javaType.simpleName} ${field.lowerCamelName}) {
        this.${field.lowerCamelName} = ${field.lowerCamelName};
    }

[/#list]
[#--toString--]
    @Override
    public String toString() {
        return "${entity.entityName}{" +
                [#list entity.fields as field]
                "[#if field_index !=0], [/#if]${field.lowerCamelName}=" + ${field.lowerCamelName} +
                [/#list]
                '}';
    }
[/#if]
}
