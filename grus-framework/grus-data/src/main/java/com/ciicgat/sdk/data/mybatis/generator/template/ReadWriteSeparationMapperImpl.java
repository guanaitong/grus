/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.template;

import com.ciicgat.sdk.data.mybatis.generator.condition.Example;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 读写分离Mapper实现父类
 *
 * <p>需要读写分离的Mapper继承此类,并添加<code>@Primary</code>注解, 其CRUD方法均实现读写分离</p>
 *
 * @param <T> 实体
 */
public class ReadWriteSeparationMapperImpl<T> implements BaseMapper<T> {

    @Autowired
    protected SqlSession readSqlSessionTemplate;
    @Autowired
    protected SqlSession writeSqlSessionTemplate;

    @Override
    public int insert(T entity) {
        return writeSqlSessionTemplate.insert(this.getStatement("insert"), entity);
    }

    @Override
    public int insertAll(T entity) {
        return writeSqlSessionTemplate.insert(this.getStatement("insertAll"), entity);
    }

    @Override
    public int batchInsert(List<T> list) {
        return writeSqlSessionTemplate.insert(this.getStatement("batchInsert"), list);
    }

    @Override
    public int delete(Long id) {
        return writeSqlSessionTemplate.delete(this.getStatement("delete"), this.buildIdParamMap(id));
    }

    @Override
    public int batchDelete(List<Long> ids) {
        return writeSqlSessionTemplate.delete(this.getStatement("batchDelete"), this.buildIdsParamMap(ids));
    }

    @Override
    public int deleteByExample(Example<T> example) {
        return writeSqlSessionTemplate.delete(this.getStatement("deleteByExample"), this.buildExampleParamMap(example));
    }

    @Override
    public int update(T entity) {
        return writeSqlSessionTemplate.update(this.getStatement("update"), this.buildEntityParamMap(entity));
    }

    @Override
    public int updateAll(T entity) {
        return writeSqlSessionTemplate.update(this.getStatement("updateAll"), this.buildEntityParamMap(entity));
    }

    @Override
    public int updateByExample(T entity, Example<T> example) {
        return writeSqlSessionTemplate.update(this.getStatement("updateByExample"), this.buildEntityAndExampleParamMap(entity, example));
    }

    @Override
    public int updateByExampleAll(T entity, Example<T> example) {
        return writeSqlSessionTemplate.update(this.getStatement("updateByExampleAll"), this.buildEntityAndExampleParamMap(entity, example));
    }

    @Override
    public T get(Long id) {
        return readSqlSessionTemplate.selectOne(this.getStatement("get"), this.buildIdParamMap(id));
    }

    @Override
    public List<T> batchGet(List<Long> ids) {
        return readSqlSessionTemplate.selectList(this.getStatement("batchGet"), this.buildIdsParamMap(ids));
    }

    @Override
    public T getByExample(Example<T> example) {
        return readSqlSessionTemplate.selectOne(this.getStatement("getByExample"), this.buildExampleParamMap(example));
    }

    @Override
    public List<T> list(Example<T> example) {
        return readSqlSessionTemplate.selectList(this.getStatement("list"), this.buildExampleParamMap(example));
    }

    @Override
    public int count(Example<T> example) {
        return readSqlSessionTemplate.selectOne(this.getStatement("count"), this.buildExampleParamMap(example));
    }

    protected String getStatement(String method) {
        Class<?> mapperType = Optional.of(this.getClass().getInterfaces())
                .map(x -> x[0]).orElseThrow(() -> new RuntimeException("Fail to get Interface of Mapper"));
        return mapperType.getName() + "." + method;
    }

    private Map<String, Object> buildParamMap(String name, Object value) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(name, value);
        return paramMap;
    }

    private Map<String, Object> buildIdParamMap(Long id) {
        return this.buildParamMap("id", id);
    }

    private Map<String, Object> buildIdsParamMap(List<Long> ids) {
        return this.buildParamMap("ids", ids);
    }

    private Map<String, Object> buildEntityAndExampleParamMap(T entity, Example<T> example) {
        Map<String, Object> paramMap = this.buildParamMap("example", example);
        paramMap.put("entity", entity);
        return paramMap;
    }

    private Map<String, Object> buildExampleParamMap(Example<T> example) {
        return this.buildParamMap("example", example);
    }

    private Map<String, Object> buildEntityParamMap(T entity) {
        return this.buildParamMap("entity", entity);
    }
}
