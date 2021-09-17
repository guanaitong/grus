/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generated.controller;

import com.ciicgat.grus.generator.cg.generated.dto.Test2VO;
import com.ciicgat.grus.generator.cg.generated.entity.Test2DO;
import com.ciicgat.grus.generator.cg.generated.service.Test2Service;
import com.ciicgat.sdk.data.mybatis.generator.condition.PageQueryExample;
import com.ciicgat.sdk.data.mybatis.generator.template.PageQueryRequest;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.Pagination;
import com.ciicgat.sdk.util.bean.BeanCopyUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 推广员 控制器
 */
@RestController
@RequestMapping("/test2")
public class Test2Controller {

    @Resource
    private Test2Service test2Service;

    @PostMapping("save")
    public ApiResponse save(Test2VO request) {
        Test2DO entity = BeanCopyUtil.copy(request, Test2DO.class);
        test2Service.save(entity);
        return ApiResponse.success(entity.getId());
    }

    @PostMapping("delete")
    public ApiResponse<Boolean> delete(Long id) {
        return ApiResponse.success(test2Service.delete(id) > 0);
    }

    @PostMapping("get")
    public ApiResponse<Test2VO> get(Long id) {
        return ApiResponse.success(BeanCopyUtil.copy(test2Service.get(id), Test2VO.class));
    }

    @PostMapping("page")
    public ApiResponse<Pagination<Test2VO>> page(@RequestBody PageQueryRequest<Test2VO> request) {
        PageQueryExample<Test2DO> query = new PageQueryExample<>();
        query.setPage(request.getPage());
        query.setRowsPerPage(request.getRowsPerPage());
        if (request.getEntity() != null) {
            query.setEntity(BeanCopyUtil.copy(request.getEntity(), Test2DO.class));
        }
        return ApiResponse.success(BeanCopyUtil.copyPagination(test2Service.page(query), Test2VO.class));
    }
}
