/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator;

import com.ciicgat.sdk.data.mybatis.generator.condition.ConditionExample;
import com.ciicgat.sdk.data.mybatis.generator.condition.Example;
import com.ciicgat.sdk.data.mybatis.generator.condition.LambdaCriteria;
import com.ciicgat.sdk.data.mybatis.generator.condition.QueryExample;
import com.ciicgat.sdk.data.mybatis.generator.entity.ShopTip;
import com.ciicgat.sdk.data.mybatis.generator.mapper.ShopTipMapper;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Clive Yuan
 * @date 2020/12/22
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MybatisGeneratorApplication.class)
@TestPropertySource("classpath:mg-application.properties")
public class MybatisGeneratorTest {

    @Resource
    private ShopTipMapper shopTipMapper;

    @Test
    public void insert() {
        Long id = add();
        checkExist(id);
    }

    @Test
    public void insertAll() {
        ShopTip entity = buildShopTip();
        shopTipMapper.insertAll(entity);
        Long id = entity.getId();
        Assertions.assertNotNull(id);
    }

    @Test
    public void insertIgnore() {
        ShopTip entity = buildShopTip();
        shopTipMapper.insertIgnore(entity);
        Assertions.assertNotNull(entity.getId());

        ShopTip entity2 = buildShopTip();
        entity2.setEcappId(1024L);
        shopTipMapper.insertIgnore(entity2);
        Assertions.assertNull(entity2.getId());
    }

    @Test
    public void batchInsert() {
        int count = 10;
        List<ShopTip> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(buildShopTip());
        }
        int c = shopTipMapper.batchInsert(list);
        Assertions.assertEquals(count, c);
        for (ShopTip shopTip : list) {
            System.out.println("batchInsert: " + shopTip);
            Assertions.assertNotNull(shopTip.getId());
        }
    }

    @Test
    public void batchInsertIgnore() {
        int count = 10;
        List<ShopTip> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ShopTip shopTip = buildShopTip();
            if (i == 0) {
                shopTip.setTitle(null);
            }
            list.add(shopTip);
        }
        int c = shopTipMapper.batchInsertIgnore(list);
        Assertions.assertEquals(count, c);
        for (ShopTip shopTip : list) {
            System.out.println("batchInsertIgnore: " + shopTip);
            Assertions.assertNotNull(shopTip.getId());
        }
    }

    @Test
    public void delete() {
        Long id = add();
        checkExist(id);
        shopTipMapper.delete(id);
        ShopTip shopTip = getById(id);
        Assertions.assertNull(shopTip);
    }

    @Test
    public void batchDelete() {
        List<ShopTip> list = new ArrayList<>();
        int count = 10;
        for (int i = 0; i < count; i++) {
            list.add(buildShopTip());
        }
        shopTipMapper.batchInsert(list);
        List<Long> ids = list.stream().map(ShopTip::getId).collect(Collectors.toList());
        List<ShopTip> shopTips = shopTipMapper.batchGet(ids);
        Assertions.assertEquals(count, shopTips.size());
        shopTipMapper.batchDelete(ids);
        shopTips = shopTipMapper.batchGet(ids);
        Assertions.assertEquals(0, shopTips.size());
    }

    @Test
    public void deleteByExample() {
        List<ShopTip> list = new ArrayList<>();
        int count = 10;
        for (int i = 0; i < count; i++) {
            list.add(buildShopTip());
        }
        shopTipMapper.batchInsert(list);
        List<Long> ids = list.stream().map(ShopTip::getId).collect(Collectors.toList());
        List<ShopTip> shopTips = shopTipMapper.batchGet(ids);
        Assertions.assertEquals(count, shopTips.size());
        Example<ShopTip> example = new QueryExample<>();
        example.createLambdaCriteria().in(ShopTip::getId, ids);
        shopTipMapper.deleteByExample(example);
        shopTips = shopTipMapper.batchGet(ids);
        Assertions.assertEquals(0, shopTips.size());
    }

    @Test
    public void update() {
        ShopTip entity = buildShopTip();
        shopTipMapper.insert(entity);
        String content = "update-" + UUID.randomUUID();
        entity.setContent(content);
//        entity.setEcappId(714962453l);
        shopTipMapper.update(entity);
        ShopTip newShopTip = getById(entity.getId());
        Assertions.assertEquals(content, newShopTip.getContent());
    }

    @Test
    public void updateAll() {
        ShopTip entity = buildShopTip();
        shopTipMapper.insert(entity);
        String content = "update-" + UUID.randomUUID();
        entity.setContent(content);
        shopTipMapper.updateAll(entity);
        ShopTip newShopTip = getById(entity.getId());
        Assertions.assertEquals(content, newShopTip.getContent());
    }

    @Test
    public void updateByExample() {
        ShopTip entity = buildShopTip();
        String content = "updateByExample-" + UUID.randomUUID();
        entity.setContent(content);
        shopTipMapper.insert(entity);
        String target = "updateByExample Success";
        entity.setTitle(target);
        Example<ShopTip> example = new QueryExample<>();
        example.createLambdaCriteria().eq(ShopTip::getContent, content);
        shopTipMapper.updateByExample(entity, example);
        ShopTip newShopTip = getById(entity.getId());
        Assertions.assertEquals(target, newShopTip.getTitle());
    }

    @Test
    public void updateByExampleAll() {
        ShopTip entity = buildShopTip();
        String content = "updateByExampleAll-" + UUID.randomUUID();
        entity.setContent(content);
        shopTipMapper.insert(entity);
        String target = "updateByExampleAll Success";
        entity.setTitle(target);
        Example<ShopTip> example = new QueryExample<>();
        example.createLambdaCriteria().eq(ShopTip::getContent, content);
        shopTipMapper.updateByExampleAll(entity, example);
        ShopTip newShopTip = getById(entity.getId());
        Assertions.assertEquals(target, newShopTip.getTitle());
    }

    @Test
    public void get() {
        Long id = add();
        ShopTip shopTip = getById(id);
        System.out.println(shopTip);
    }

    @Test
    public void batchGet() {
        List<ShopTip> list = new ArrayList<>();
        int count = 10;
        for (int i = 0; i < count; i++) {
            list.add(buildShopTip());
        }
        shopTipMapper.batchInsert(list);
        List<Long> ids = list.stream().map(ShopTip::getId).collect(Collectors.toList());
        List<ShopTip> shopTips = shopTipMapper.batchGet(ids);
        Assertions.assertEquals(count, shopTips.size());
    }

    @Test
    public void getByExample() {
        Long id = add();
        Example<ShopTip> example = new QueryExample<>();
        example.createLambdaCriteria().eq(ShopTip::getId, id);
        ShopTip shopTip = shopTipMapper.getByExample(example);
        System.out.println(shopTip);
        Assertions.assertNotNull(shopTip);
    }

    @Test
    public void list() {
        String content = "list-" + UUID.randomUUID();
        ShopTip entity = buildShopTip();
        entity.setContent(content);
        shopTipMapper.insert(entity);

        Example<ShopTip> example = new ConditionExample<>();
        example.createLambdaCriteria().eq(ShopTip::getContent, content);
        List<ShopTip> list = shopTipMapper.list(example);
        Assertions.assertTrue(list.size() > 0);
        list.forEach(System.out::println);
        Assertions.assertEquals(content, list.get(0).getContent());
    }

    @Test
    public void count() {
        List<ShopTip> list = new ArrayList<>();
        int count = 10;
        for (int i = 0; i < count; i++) {
            list.add(buildShopTip());
        }
        shopTipMapper.batchInsert(list);

        Example<ShopTip> example = new ConditionExample<>();
        List<Long> ids = list.stream().map(ShopTip::getId).collect(Collectors.toList());
        example.createLambdaCriteria().in(ShopTip::getId, ids);
        int total = shopTipMapper.count(example);
        Assertions.assertEquals(count, total);
    }

    @Test
    public void condition() {
        String content = "condition-" + UUID.randomUUID();
        ShopTip entity = buildShopTip();
        entity.setContent(content);
        shopTipMapper.insert(entity);
        Integer type = null;
        Example<ShopTip> example = new ConditionExample<>();
        example.createLambdaCriteria().eq(ShopTip::getContent, content).eq(ShopTip::getType, type);
        List<ShopTip> list = shopTipMapper.list(example);
        Assertions.assertTrue(list.size() > 0);
        list.forEach(System.out::println);
        Assertions.assertEquals(content, list.get(0).getContent());
    }

    @Test
    public void codeCover() {
        Example<ShopTip> example = new ConditionExample<>();
        LambdaCriteria<ShopTip> criteria = example.createLambdaCriteria()
                .eq(ShopTip::getContent, "")
                .eq(ShopTip::getType, null)
                .ne(ShopTip::getEnable, false)
                .gt(ShopTip::getId, 100L)
                .ge(ShopTip::getId, 100L)
                .le(ShopTip::getId, 1000L)
                .lt(ShopTip::getId, 1000L)
                .isBlank(ShopTip::getEnable)
                .isNotBlank(ShopTip::getTitle)
                .isNull(ShopTip::getEnable)
                .isNotNull(ShopTip::getEnable)
                .between(ShopTip::getId, 1L, 100L)
                .notBetween(ShopTip::getType, 1, 10)
                .like(ShopTip::getTitle, "gmall")
                .notLike(ShopTip::getTitle, "gmall")
                .likeLeft(ShopTip::getTitle, "gmall")
                .likeRight(ShopTip::getTitle, "gmall")
                .in(ShopTip::getId, 1, 2, 3)
                .notIn(ShopTip::getId, 4, 5, 6);
        Assertions.assertTrue(criteria.isValid());
        LambdaCriteria<ShopTip> criteria1 = example.createLambdaCriteria();
        Assertions.assertFalse(criteria1.isValid());
        criteria1.setCriteria(Collections.emptyList());
        try {
            criteria1.between(ShopTip::getTimeCreated, null, "2")
                    .notBetween(ShopTip::getTimeCreated, "null", null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        ShopTip shopTip = shopTipMapper.getByExample(example);
        Assertions.assertNull(shopTip);
    }

    @Test
    public void updateAssert() {
        ShopTip entity = new ShopTip();
        entity.setEnable(0);
        Example<ShopTip> example = new QueryExample<>();
        example.createLambdaCriteria().in(ShopTip::getId, 2, 3, 4)
                .eq(ShopTip::getContent, null);
        List<ShopTip> list = shopTipMapper.list(example);
        Assertions.assertEquals(3, list.size());
        boolean throwError = false;
        try {
            Example<ShopTip> example2 = new QueryExample<>();
            example2.createLambdaCriteria().in(ShopTip::getId, 2, 3, 4)
                    .eq(ShopTip::getContent, null);
            shopTipMapper.updateByExample(entity, example2);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throwError = true;
        }
        Assertions.assertTrue(throwError);
    }

    private ShopTip buildShopTip() {
        ShopTip shopTip = new ShopTip();
        shopTip.setEcappId(RandomUtils.nextLong(10000000, 999999999));
        shopTip.setContent("CLIVE-test");
        shopTip.setEnable(1);
        shopTip.setType(1);
        shopTip.setTitle("Junit-test" + UUID.randomUUID().toString());
        return shopTip;
    }

    private Long add() {
        ShopTip entity = buildShopTip();
        shopTipMapper.insert(entity);
        Long id = entity.getId();
        Assertions.assertNotNull(id);
        return id;
    }

    private void checkExist(Long id) {
        Assertions.assertNotNull(getById(id));
    }

    private ShopTip getById(Long id) {
        Assertions.assertNotNull(id);
        return shopTipMapper.get(id);
    }

}
