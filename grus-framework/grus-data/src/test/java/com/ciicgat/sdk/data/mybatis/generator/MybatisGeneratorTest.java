/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator;

import com.ciicgat.sdk.data.mybatis.generator.condition.ConditionExample;
import com.ciicgat.sdk.data.mybatis.generator.condition.Example;
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
