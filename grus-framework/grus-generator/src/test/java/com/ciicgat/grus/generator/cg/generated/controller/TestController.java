/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generated.controller;

import com.ciicgat.grus.generator.cg.generated.dto.TestVO;
import com.ciicgat.grus.generator.cg.generated.entity.TestDO;
import com.ciicgat.grus.generator.cg.generated.service.TestService;
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
 * 测试 控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    @PostMapping("save")
    public ApiResponse save(TestVO request) {
        TestDO entity = BeanCopyUtil.copy(request, TestDO.class);
        testService.save(entity);
        return ApiResponse.success(entity.getId());
    }

    @PostMapping("delete")
    public ApiResponse<Boolean> delete(Long id) {
        return ApiResponse.success(testService.delete(id) > 0);
    }

    @PostMapping("get")
    public ApiResponse<TestVO> get(Long id) {
        return ApiResponse.success(BeanCopyUtil.copy(testService.get(id), TestVO.class));
    }

    @PostMapping("page")
    public ApiResponse<Pagination<TestVO>> page(@RequestBody PageQueryRequest<TestVO> request) {
        PageQueryExample<TestDO> query = new PageQueryExample<>();
        query.setPage(request.getPage());
        query.setRowsPerPage(request.getRowsPerPage());
        if (request.getEntity() != null) {
            query.setEntity(BeanCopyUtil.copy(request.getEntity(), TestDO.class));
        }
        return ApiResponse.success(BeanCopyUtil.copyPagination(testService.page(query), TestVO.class));
    }
}
