/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import com.ciicgat.grus.elasticsearch.annotations.Document;
import com.ciicgat.sdk.lang.convert.Pagination;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by August.Zhou on 2019-08-22 17:47.
 */
public class ElasticsearchTemplate<T extends IndexAble> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchTemplate.class);
    private final RestHighLevelClient restHighLevelClient;
    private final String index;
    private final IndexSuffixType indexSuffixType;
    private final Class<T> clazz;
    private final EntityMapper<T> entityMapper;
    private final Document document;
    private RequestOptions options = RequestOptions.DEFAULT;

    private final RateLimiter saveRateLimiter = RateLimiter.create(20);

    public ElasticsearchTemplate(RestHighLevelClient restHighLevelClient, Class<T> clazz) {
        this(restHighLevelClient, clazz, new DefaultEntityMapper<>(clazz));
    }

    public ElasticsearchTemplate(RestHighLevelClient restHighLevelClient, Class<T> clazz, EntityMapper<T> entityMapper) {
        this.clazz = clazz;
        Document document = clazz.getAnnotation(Document.class);
        checkDocument(document);
        this.document = document;
        this.restHighLevelClient = restHighLevelClient;
        this.index = document.index();
        this.indexSuffixType = document.indexSuffixType();
        this.entityMapper = entityMapper;
    }

    public void setSaveQps(double saveQps) {
        saveRateLimiter.setRate(saveQps);
    }


    private void checkDocument(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("clazz must annotate @document");
        }
        if (StringUtils.isBlank(document.index())) {
            throw new IllegalArgumentException("index should set");
        }
        if (StringUtils.isBlank(document.alias())) {
            throw new IllegalArgumentException("alias should set");
        }
    }

    public void setOptions(RequestOptions options) {
        this.options = Objects.requireNonNull(options);
    }

    public String getAlias() {
        return document.alias();
    }

    /**
     * 获取当前时间对应的索引名。如果IndexSuffixType为非滚动，那么就是设置的原始值
     *
     * @return 索引
     */
    public String getCurrentIndex() {
        return indexSuffixType.getCurrentIndex(index);
    }

    /**
     * 获取当前时间对应的索引名字，如果save时，对象指定了索引，则使用指定的索引
     *
     * @param value save的对象
     * @return 索引
     */
    private String getCurrentIndex(T value) {
        return StringUtils.isNotBlank(value.getDocIndex()) ? value.getDocIndex() : indexSuffixType.getCurrentIndex(this.index, value.getTimestampSupplier());
    }

    /**
     * 获取下一个阶段对应的索引名。如果IndexSuffixType为非滚动，那么就是设置的原始值
     *
     * @return
     */
    public String getNextIndex() {
        return indexSuffixType.getNextIndex(index);
    }

    public boolean existCurrentIndex() {
        String index = getCurrentIndex();
        return existIndex(index);
    }

    public boolean existIndex(String... indices) {
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indices);
            IndicesClient indicesClient = restHighLevelClient.indices();
            return indicesClient.exists(getIndexRequest, options);
        } catch (IOException e) {
            throw new EsDataException(e);
        }
    }

    public boolean createIndex() {
        return createIndex(false);
    }

    public boolean createIndex(boolean next) {
        String index = next ? getNextIndex() : getCurrentIndex();
        return createIndex(index);
    }

    public boolean createIndex(String index) {
        IndicesClient indices = restHighLevelClient.indices();
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(index);
            boolean exists = indices.exists(getIndexRequest, options);
            if (!exists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                //设置别名
                if (StringUtils.isNotEmpty(document.alias())) {
                    createIndexRequest.alias(new Alias(document.alias()));
                }
                //设置settings
                if (StringUtils.isEmpty(document.settingPath())) {
                    Settings.Builder settings = Settings.builder()
                            .put("index.number_of_shards", document.shards())
                            .put("index.number_of_replicas", document.replicas());
                    if (document.max_result_window() > 0) {
                        settings.put("index.max_result_window", document.max_result_window());
                    }
                    createIndexRequest.settings(settings);
                } else {
                    createIndexRequest.settings(getFileContent(document.settingPath()), XContentType.JSON);
                }
                //设置mapping
                createIndexRequest.mapping(getFileContent(document.mappingPath()), XContentType.JSON);
                CreateIndexResponse createIndexResponse = indices.create(createIndexRequest, options);
                if (!createIndexResponse.isAcknowledged()) {
                    LOGGER.error("创建索引[{}]发生异常", index);
                }
                return true;
            }
        } catch (IOException e) {
            throw new EsDataException(e);
        }
        return false;
    }

    private static String getFileContent(final String filePath) {
        String file = filePath;
        if (!file.startsWith("/")) {
            file = "/" + file;
        }
        try (InputStream in = ElasticsearchTemplate.class.getResourceAsStream(file)) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EsDataException(e);
        }
    }

    public int save(T value) {
        return save(false, value);
    }

    public int save(boolean sync, T value) {
        String index = getCurrentIndex(value);
        IndexRequest request = new IndexRequest(index)
                .id(value.getDocId())
                .source(entityMapper.mapToString(value), XContentType.JSON)
                .setRefreshPolicy(sync ? WriteRequest.RefreshPolicy.WAIT_UNTIL : WriteRequest.RefreshPolicy.NONE)
                .opType(DocWriteRequest.OpType.INDEX);
        String pipeline = value.getPipelineSupplier().get();
        if (StringUtils.isNotEmpty(pipeline)) {
            request.setPipeline(pipeline);
        }
        try {
            saveRateLimiter.acquire();
            IndexResponse indexResponse = restHighLevelClient.index(request, options);
            int status = indexResponse.status().getStatus();
            return (status >= 200 && status < 300) ? 1 : 0;
        } catch (IOException e) {
            throw new EsDataException(e);
        }
    }

    public void save(T[] value) {
        save(false, Arrays.asList(value));
    }

    public int save(boolean sync, List<T> dataList) {
        if (dataList.size() == 1) {
            return save(sync, dataList.get(0));
        }

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(sync ? WriteRequest.RefreshPolicy.WAIT_UNTIL : WriteRequest.RefreshPolicy.NONE);
        for (T t : dataList) {
            // 如果用户自己设置了docIndex，那么使用用户设置的。否则使用框架的规则
            String index = getCurrentIndex(t);
            IndexRequest request = new IndexRequest(index)
                    .id(t.getDocId())
                    .source(entityMapper.mapToString(t), XContentType.JSON)
                    .opType(DocWriteRequest.OpType.INDEX);

            String pipeline = t.getPipelineSupplier().get();
            if (StringUtils.isNotEmpty(pipeline)) {
                request.setPipeline(pipeline);
            }
            bulkRequest.add(request);
        }
        try {
            saveRateLimiter.acquire(dataList.size());
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, options);
            if (bulkResponse.status() != RestStatus.OK) {
                return 0;
            }
            int failedCount = 0;
            // 处理错误信息
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.isFailed()) {
                        failedCount++;
                        LOGGER.warn("索引 " + index + " 发生错误的 索引id为 : " + bulkItemResponse.getId() + " ，错误信息为：" + bulkItemResponse.getFailureMessage());
                    }
                }
            }
            return dataList.size() - failedCount;
        } catch (IOException e) {
            throw new EsDataException(e);
        }
    }

//    public int update(T value) {
//        String index = indexSuffixType.getCurrentIndex(this.index, value.getTimestampSupplier());
//        try {
//            UpdateRequest indexRequest = new UpdateRequest(index, value.getDocId()).doc(entityMapper.mapToString(value), XContentType.JSON);
//            UpdateResponse updateResponse = restHighLevelClient.update(indexRequest, options);
//            return updateResponse.status() == RestStatus.OK ? 1 : 0;
//        } catch (IOException e) {
//            throw new EsDataException(e);
//        }
//    }

    public int delete(String id) {
        String index = getCurrentIndex();
        try {
            DeleteRequest deleteRequest = new DeleteRequest(index, id);
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, options);
            return deleteResponse.status() == RestStatus.OK ? 1 : 0;
        } catch (IOException e) {
            throw new EsDataException(e);
        }
    }


    public T get(String id) {
        return get(id, document.alias());
    }

    public T get(String id, String index) {
        try {
            GetRequest getRequest = new GetRequest(index).id(id);
            GetResponse getResponse = restHighLevelClient.get(getRequest, options);
            if (getResponse.isExists()) {
                T object = entityMapper.mapToObject(getResponse.getSourceAsString(), clazz);
                object.setDocId(id);
                object.setDocIndex(getResponse.getIndex());
                return object;
            }
        } catch (IOException e) {
            throw new EsDataException(e);
        }
        return null;
    }

    private SearchResponse search0(SearchRequest searchRequest) {
        try {
            return restHighLevelClient.search(searchRequest, options);
        } catch (IOException e) {
            throw new EsDataException(e);
        }
    }

    public Pagination<T> parseSearchResponse(SearchResponse searchResponse) {
        SearchHits searchHits = searchResponse.getHits();
        TotalHits totalHits = searchHits.getTotalHits();
        List<T> resultList = new ArrayList<>();
        for (SearchHit searchHit : searchHits.getHits()) {
            T object = entityMapper.mapToObject(searchHit.getSourceAsString(), clazz);
            object.setDocId(searchHit.getId());
            object.setDocIndex(searchHit.getIndex());
            resultList.add(object);
        }
        return new Pagination<>((int) totalHits.value, resultList);
    }

    public Pagination<T> search(SearchRequest searchRequest) {
        SearchResponse searchResponse = search0(searchRequest);
        return parseSearchResponse(searchResponse);
    }

    public <E> E search(SearchRequest searchRequest, Function<SearchResponse, E> procHandler) {
        SearchResponse searchResponse = search0(searchRequest);
        return procHandler.apply(searchResponse);
    }

    /**
     * 获取索引后缀类型
     *
     * @return
     */
    public IndexSuffixType getIndexSuffixType() {
        return indexSuffixType;
    }
}
