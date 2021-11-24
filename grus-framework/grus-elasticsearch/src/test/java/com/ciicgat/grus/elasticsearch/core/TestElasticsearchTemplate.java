/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import com.ciicgat.grus.elasticsearch.Message;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.lang.convert.Pagination;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @author wanchongyang
 * @date 2021/11/9 7:02 下午
 */
public class TestElasticsearchTemplate {
    private static ElasticsearchTemplate<Message> elasticsearchTemplate;

    @BeforeAll
    public static void init() {
        List<HttpHost> httpHosts = new ArrayList<>(3);
        Properties properties = GlobalGconfConfig.getConfig().getProperties("address.properties");
        String esServerLists = properties.getProperty("esServerLists", "app-es.servers.dev.ofc:9200");
        Stream.of(esServerLists.split(",")).forEach(node -> {
            String[] s = node.split(":");
            httpHosts.add(new HttpHost(s[0], Integer.parseInt(s[1])));
        });

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(httpHosts.toArray(new HttpHost[0])));
        elasticsearchTemplate = new ElasticsearchTemplate<>(client, Message.class);
        // 创建索引
        elasticsearchTemplate.createIndex();
    }

    @Test
    public void testSimple() {
        String docId = "test" + System.currentTimeMillis();
        String appName = "frigate-message";
        String docIndex = elasticsearchTemplate.getCurrentIndex();
        Message message = new Message();
        message.setDocIndex(docIndex);
        message.setChannel(1);
        message.setAppName(appName);
        message.setContent("消息内容test");
        message.setStatus(0);
        message.setWorkEnv(WorkRegion.getCurrentWorkRegion().getWorkEnv().getName());
        message.setWorkIdc(WorkRegion.getCurrentWorkRegion().getWorkIdc().getInnerDomainSuffix());
        message.setTimeCreated(new Date());
        message.setDocId(docId);

        // 新增
        int saveResult = elasticsearchTemplate.save(message, true);
        Assertions.assertEquals(1, saveResult);

        // 查询
        Message searchResult = elasticsearchTemplate.get(docId);
        Assertions.assertEquals(message, searchResult);

        // 更新
        Message updateMessage = new Message();
        updateMessage.setDocId(docId);
        updateMessage.setStatus(1);
        updateMessage.setDocIndex(docIndex);
        int update = elasticsearchTemplate.update(updateMessage, true);
        Assertions.assertEquals(1, update);
        Message updateSearchResult = elasticsearchTemplate.get(docId);
        // 只更新变更字段
        Assertions.assertEquals(1, updateSearchResult.getStatus());
        // 其他字段不更新
        Assertions.assertEquals(appName, updateSearchResult.getAppName());

        // 删除
        int deleteResult = elasticsearchTemplate.delete(docId);
        Assertions.assertEquals(1, deleteResult);
    }

    @Test
    public void testBatch() {
        List<Message> messageList = new ArrayList<>();
        int count = 5;
        for (int index = 1; index <= count; index++) {
            Message message = new Message();
            message.setDocId("batch" + index + "_" + System.currentTimeMillis());
            message.setChannel(1);
            message.setAppName("frigate-message");
            message.setContent("消息内容test".concat(" ").concat(message.getDocId()));
            message.setStatus(0);
            message.setWorkEnv(WorkRegion.getCurrentWorkRegion().getWorkEnv().getName());
            message.setWorkIdc(WorkRegion.getCurrentWorkRegion().getWorkIdc().getInnerDomainSuffix());
            message.setTimeCreated(new Date());
            messageList.add(message);
        }

        int saveResult = elasticsearchTemplate.save(messageList, true);
        Assertions.assertEquals(count, saveResult);
    }

    @Test
    public void testSearch() {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10);
        searchSourceBuilder.sort("timeCreated", SortOrder.DESC);
        searchSourceBuilder.trackTotalHits(true);

        SearchRequest searchRequest = new SearchRequest(elasticsearchTemplate.getAlias());
        searchRequest.source(searchSourceBuilder);

        Pagination<Message> pagination = elasticsearchTemplate.search(searchRequest);
        Assertions.assertNotNull(pagination.getDataList());
    }
}
