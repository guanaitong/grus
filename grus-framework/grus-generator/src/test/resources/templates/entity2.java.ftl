package ${entityPackage};

import com.ciicgat.sdk.data.mybatis.generator.annotation.TableField;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableId;
import com.ciicgat.sdk.data.mybatis.generator.annotation.TableName;
[#if baseConfig.enableSwagger]
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
[/#if]
import java.io.Serializable;
import java.util.Date;
[#if baseConfig.enableLombok]
import lombok.Data;
[#else]
import com.ciicgat.grus.json.JSON;
[/#if]
[#if baseConfig.enableValidation]
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
[/#if]
[#if entity.hasBigDecimalField]
import java.math.BigDecimal;
[/#if]


/**
 * ${entity.comment}2222
 */
[#if baseConfig.enableLombok]
@Data
[/#if]
[#if baseConfig.enableSwagger]
@ApiModel(value = "${entity.comment}")
[/#if]
@TableName("${entity.tableName}")
public class ${entity.entityName} implements Serializable {

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
    [#if field.markColumnName || field.ignoreSaving]
    @TableField([#if field.markColumnName]value = "${field.columnName}"[/#if][#if field.markColumnName && field.ignoreSaving], [/#if][#if field.ignoreSaving]ignoreSaving = true[/#if])
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
        return JSON.toJSONString(this);
    }
[/#if]
}
