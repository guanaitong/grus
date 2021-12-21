/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator;

import com.ciicgat.sdk.data.mybatis.generator.condition.ConditionExample;
import com.ciicgat.sdk.data.mybatis.generator.condition.Example;
import com.ciicgat.sdk.data.mybatis.generator.condition.LambdaCriteria;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
    public void delete() {
        Long id = add();
        checkExist(id);

        shopTipMapper.delete(id);

        ShopTip shopTip = getById(id);

        Assertions.assertNull(shopTip);
    }

    @Test
    public void update() {
        ShopTip entity = buildShopTip();
        shopTipMapper.insert(entity);

        String content = "update-" + UUID.randomUUID().toString();

        entity.setContent(content);
//        entity.setEcappId(714962453l);
        shopTipMapper.update(entity);

        ShopTip newShopTip = getById(entity.getId());

        Assertions.assertEquals(content, newShopTip.getContent());

    }

    @Test
    public void get() {
        Long id = add();
        ShopTip shopTip = getById(id);
        System.out.println(shopTip);
    }

    @Test
    public void list() {
        String content = "list-" + UUID.randomUUID().toString();
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
    public void condition() {
        String content = "condition-" + UUID.randomUUID();
        ShopTip entity = buildShopTip();
        entity.setContent(content);
        shopTipMapper.insert(entity);
        Integer type = null;
        Example<ShopTip> example = new ConditionExample<>();
        example.createLambdaCriteria().eq(ShopTip::getContent, content).eq(ShopTip::getType, type).neBlankable(ShopTip::getEcappId, "");
        List<ShopTip> list = shopTipMapper.list(example);
        Assert.assertTrue(list.size() > 0);
        list.forEach(System.out::println);
        Assert.assertEquals(content, list.get(0).getContent());
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
                .neBlankable(ShopTip::getEcappId, "")
                .eqBlankable(ShopTip::getContent, "")
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
        Assert.assertTrue(criteria.isValid());
        LambdaCriteria<ShopTip> criteria1 = example.createLambdaCriteria();
        Assert.assertFalse(criteria1.isValid());
        criteria1.setCriteria(Collections.emptyList());
        try {
            criteria1.addCriterion(null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            criteria1.between(ShopTip::getTimeCreated, null, "2")
                    .notBetween(ShopTip::getTimeCreated, "null", null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        ShopTip shopTip = shopTipMapper.getByExample(example);
        Assert.assertNull(shopTip);
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
