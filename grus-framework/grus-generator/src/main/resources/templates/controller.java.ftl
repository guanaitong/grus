/*
 * Copyright 2007-${.now?string('yyyy')}, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package ${controllerPackage};

import ${entityPackage}.${entity.entityName};
import ${dtoPackage}.${entity.upperCamelName}${dtoSuffix};
import ${servicePackage}.${entity.upperCamelName}${serviceSuffix};
import com.ciicgat.sdk.data.mybatis.generator.condition.PageQueryExample;
import com.ciicgat.sdk.data.mybatis.generator.template.PageQueryRequest;
import com.ciicgat.sdk.util.bean.BeanCopyUtil;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.Pagination;

[#if baseConfig.enableSwagger]
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
[/#if]
[#if baseConfig.enableValidation]
import org.springframework.validation.annotation.Validated;
[/#if]
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * ${entity.comment} 控制器
 */
@RestController
@RequestMapping("/${entity.lowerCamelName}")
[#if baseConfig.enableSwagger]
@Api(tags = "${entity.comment}接口")
[/#if]
public class ${entity.fileName} {

    @Resource
    private ${entity.upperCamelName}${serviceSuffix} ${entity.lowerCamelName}${serviceSuffix};

    @PostMapping("save")
[#if baseConfig.enableSwagger]
    @ApiOperation("保存")
[/#if]
    public ApiResponse save([#if baseConfig.enableValidation]@Validated [/#if]${entity.upperCamelName}${dtoSuffix} request) {
        ${entity.entityName} entity = BeanCopyUtil.copy(request, ${entity.entityName}.class);
        ${entity.lowerCamelName}${serviceSuffix}.save(entity);
        return ApiResponse.success(entity.getId());
    }

    @PostMapping("delete")
[#if baseConfig.enableSwagger]
    @ApiOperation("删除")
[/#if]
    public ApiResponse<Boolean> delete(Long id) {
        return ApiResponse.success(${entity.lowerCamelName}${serviceSuffix}.delete(id) > 0);
    }

    @PostMapping("get")
[#if baseConfig.enableSwagger]
    @ApiOperation("获取")
[/#if]
    public ApiResponse<${entity.upperCamelName}${dtoSuffix}> get(Long id) {
        return ApiResponse.success(BeanCopyUtil.copy(${entity.lowerCamelName}${serviceSuffix}.get(id), ${entity.upperCamelName}${dtoSuffix}.class));
    }

    @PostMapping("page")
[#if baseConfig.enableSwagger]
    @ApiOperation("分页查询")
[/#if]
    public ApiResponse<Pagination<${entity.upperCamelName}${dtoSuffix}>> page(@RequestBody PageQueryRequest<${entity.upperCamelName}${dtoSuffix}> request) {
        PageQueryExample<${entity.entityName}> query = new PageQueryExample<>();
        query.setPage(request.getPage());
        query.setRowsPerPage(request.getRowsPerPage());
        if (request.getEntity() != null) {
            query.setEntity(BeanCopyUtil.copy(request.getEntity(), ${entity.entityName}.class));
        }
        return ApiResponse.success(BeanCopyUtil.copyPagination(${entity.lowerCamelName}${serviceSuffix}.page(query), ${entity.upperCamelName}${dtoSuffix}.class));
    }
}
