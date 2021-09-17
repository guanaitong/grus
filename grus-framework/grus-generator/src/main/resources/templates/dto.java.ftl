/*
 * Copyright 2007-${.now?string('yyyy')}, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package ${dtoPackage};

[#if baseConfig.enableSwagger]
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
[/#if]
[#if baseConfig.enableValidation]
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
[/#if]
import java.io.Serializable;
import java.util.Date;
[#if baseConfig.enableLombok]
import lombok.Data;
[/#if]

[#if entity.hasBigDecimalField]
import java.math.BigDecimal;
[/#if]


/**
 * ${entity.comment}
 */
[#if baseConfig.enableLombok]
@Data
[/#if]
[#if baseConfig.enableSwagger]
@ApiModel(value = "${entity.comment}")
[/#if]
public class ${entity.fileName} implements Serializable {

[#--fields--]
    private static final long serialVersionUID = 1L;

[#list entity.fields as field]
    /**
     * ${field.comment}
     */
    [#if baseConfig.enableSwagger]
    @ApiModelProperty(value = "${field.comment}"[#if field.swaggerHidden], hidden = true[/#if])
    [/#if]
    [#if baseConfig.enableValidation]
    [#if !field.nullable]
    @NotNull(message = "${field.comment}不能为空")
    [/#if]
    [#if field.jdbcType.javaType.simpleName == 'String']
    [#assign fieldLength = field.length?string('#')]
    @Length(max = ${fieldLength}, message = "${field.comment}长度不能超过${fieldLength}个字符")
    [/#if]
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
