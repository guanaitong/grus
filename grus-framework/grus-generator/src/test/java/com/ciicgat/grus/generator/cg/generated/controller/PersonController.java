/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.generated.controller;

import com.ciicgat.grus.generator.cg.generated.entity.PersonDO;
import com.ciicgat.grus.generator.cg.generated.dto.PersonVO;
import com.ciicgat.grus.generator.cg.generated.service.PersonService;
import com.ciicgat.sdk.data.mybatis.generator.condition.PageQueryExample;
import com.ciicgat.sdk.data.mybatis.generator.template.PageQueryRequest;
import com.ciicgat.sdk.util.bean.BeanCopyUtil;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.Pagination;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 个人 控制器
 */
@RestController
@RequestMapping("/person")
public class PersonController {

    @Resource
    private PersonService personService;

    @PostMapping("save")
    public ApiResponse save(PersonVO request) {
        PersonDO entity = BeanCopyUtil.copy(request, PersonDO.class);
        personService.save(entity);
        return ApiResponse.success(entity.getId());
    }

    @PostMapping("delete")
    public ApiResponse<Boolean> delete(Long id) {
        return ApiResponse.success(personService.delete(id) > 0);
    }

    @PostMapping("get")
    public ApiResponse<PersonVO> get(Long id) {
        return ApiResponse.success(BeanCopyUtil.copy(personService.get(id), PersonVO.class));
    }

    @PostMapping("page")
    public ApiResponse<Pagination<PersonVO>> page(@RequestBody PageQueryRequest<PersonVO> request) {
        PageQueryExample<PersonDO> query = new PageQueryExample<>();
        query.setPage(request.getPage());
        query.setRowsPerPage(request.getRowsPerPage());
        if (request.getEntity() != null) {
            query.setEntity(BeanCopyUtil.copy(request.getEntity(), PersonDO.class));
        }
        return ApiResponse.success(BeanCopyUtil.copyPagination(personService.page(query), PersonVO.class));
    }
}
