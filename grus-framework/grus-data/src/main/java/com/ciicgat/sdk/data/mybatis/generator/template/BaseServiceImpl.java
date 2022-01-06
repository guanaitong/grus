/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.template;

import com.ciicgat.sdk.data.mybatis.generator.condition.Criteria;
import com.ciicgat.sdk.data.mybatis.generator.condition.Example;
import com.ciicgat.sdk.data.mybatis.generator.condition.GeneratedCriteria;
import com.ciicgat.sdk.data.mybatis.generator.condition.LambdaCriteria;
import com.ciicgat.sdk.data.mybatis.generator.condition.PageQueryExample;
import com.ciicgat.sdk.data.mybatis.generator.condition.Query;
import com.ciicgat.sdk.data.mybatis.generator.util.ReflectUtils;
import com.ciicgat.sdk.lang.convert.Pagination;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 基础 Service  实现类
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
@SuppressWarnings({"serial", "unchecked"})
public class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    private BaseMapper<T> baseMapper;

    @Override
    public int insert(T entity) {
        this.checkEntity(entity);
        return baseMapper.insert(entity);
    }

    @Override
    public int insertAll(T entity) {
        this.checkEntity(entity);
        return baseMapper.insertAll(entity);
    }

    @Override
    public int insertIgnore(T entity) {
        this.checkEntity(entity);
        return baseMapper.insertIgnore(entity);
    }

    @Override
    public int save(T entity) {
        this.checkEntity(entity);
        return ReflectUtils.isIdNull(entity) ? this.insert(entity) : this.update(entity);
    }

    @Override
    public int saveAll(T entity) {
        this.checkEntity(entity);
        return ReflectUtils.isIdNull(entity) ? this.insertAll(entity) : this.updateAll(entity);
    }

    @Override
    public int batchInsert(List<T> list) {
        this.checkEntityList(list);
        return baseMapper.batchInsert(list);
    }

    @Override
    public int batchInsertIgnore(List<T> list) {
        this.checkEntityList(list);
        return baseMapper.batchInsertIgnore(list);
    }

    @Override
    public int delete(Long id) {
        this.checkId(id);
        return baseMapper.delete(id);
    }

    @Override
    public int batchDelete(List<Long> ids) {
        this.checkIdList(ids);
        return baseMapper.batchDelete(ids);
    }

    @Override
    public int deleteByExample(Query<T> query) {
        this.checkQuery(query);
        return baseMapper.deleteByExample(this.convertQueryToExample(query));
    }

    @Override
    public int update(T entity) {
        this.checkEntity(entity);
        return baseMapper.update(entity);
    }

    @Override
    public int updateAll(T entity) {
        this.checkEntity(entity);
        return baseMapper.updateAll(entity);
    }

    @Override
    public int updateByExample(T entity, Query<T> query) {
        this.checkEntity(entity);
        return baseMapper.updateByExample(entity, this.convertQueryToExample(query));
    }

    @Override
    public int updateByExampleAll(T entity, Query<T> query) {
        this.checkEntity(entity);
        return baseMapper.updateByExampleAll(entity, this.convertQueryToExample(query));
    }

    @Override
    public T get(Long id) {
        this.checkId(id);
        return baseMapper.get(id);
    }

    @Override
    public List<T> batchGet(List<Long> ids) {
        this.checkIdList(ids);
        return baseMapper.batchGet(ids);
    }

    @Override
    public T getByExample(Query<T> query) {
        this.checkQuery(query);
        return baseMapper.getByExample(this.convertQueryToExample(query));
    }

    @Override
    public int count(Query<T> query) {
        this.checkQuery(query);
        return baseMapper.count(this.convertQueryToExample(query));
    }

    @Override
    public List<T> list(Query<T> query) {
        this.checkQuery(query);
        return baseMapper.list(this.convertQueryToExample(query));
    }

    @Override
    public Pagination<T> page(PageQueryExample<T> query) {
        this.checkQuery(query);
        Example<T> example = this.convertQueryToExample(query);
        int page = this.getPage(query.getPage());
        int rowsPerPage = this.getRowsPerPage(query.getRowsPerPage());
        example.setLimitStart(this.getOffset(page, rowsPerPage));
        example.setLimitEnd(rowsPerPage);
        int totalCount = baseMapper.count(example);
        if (totalCount == 0) {
            return new Pagination<>(totalCount, Lists.newArrayList(), page, rowsPerPage);
        }
        return new Pagination<>(totalCount, baseMapper.list(example), page, rowsPerPage);
    }

    private Class<?> getEntityClass() {
        return ReflectUtils.getSuperClassGenericType(getClass(), 0);
    }

    private Example<T> convertQueryToExample(Query<T> query) {
        T entity = query.getEntity();
        if (Objects.nonNull(entity)) {
            Map<String, Object> fieldAndValue = ReflectUtils.resolveEntityFieldAndValue(entity);
            GeneratedCriteria criteria = query.getExistCriteria();
            if (criteria instanceof LambdaCriteria) {
                LambdaCriteria lambdaCriteria = (LambdaCriteria) criteria;
                fieldAndValue.forEach(lambdaCriteria::eq);
            } else if (criteria instanceof Criteria) {
                fieldAndValue.forEach(criteria::eq);
            }
        }
        return (Example<T>) query;
    }

    private void checkId(Long id) {
        Assert.notNull(id, "id can't be null");
    }

    private void checkEntity(T entity) {
        Assert.notNull(entity, "entity can't be null");
    }

    private void checkQuery(Query<T> query) {
        Assert.notNull(query, "query can't be null");
    }

    private void checkIdList(List<Long> idList) {
        Assert.isTrue(Objects.nonNull(idList) && idList.size() > 0, "ids can't be empty");
    }

    private void checkEntityList(List<T> list) {
        Assert.isTrue(Objects.nonNull(list) && list.size() > 0, "list can't be empty");
    }

    private int getPage(Integer page) {
        if (Objects.isNull(page) || page <= 0) {
            page = PageQueryExample.DEFAULT_PAGE_NO;
        }
        return page;
    }

    private int getRowsPerPage(Integer rowsPerPage) {
        if (Objects.isNull(rowsPerPage) || rowsPerPage <= 0) {
            rowsPerPage = PageQueryExample.DEFAULT_PAGE_SIZE;
        }
        return rowsPerPage;
    }

    private int getOffset(int page, int rowsPerPage) {
        return (page - 1) * rowsPerPage;
    }
}
