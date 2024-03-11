/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.elasticsearch.annotations.Document;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.lang.convert.Pagination;
import com.ciicgat.sdk.util.system.WorkRegion;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 * Created by August.Zhou on 2019-08-22 17:47.
 */
public class ElasticsearchTemplate<T extends IndexAble> implements MeterBinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchTemplate.class);
    private static final SlowLogger<String> SLOW_LOGGER = new SlowLogger<>(Module.ELASTICSEARCH, v -> v);
    private final RestHighLevelClient restHighLevelClient;
    private final String index;
    private final IndexSuffixType indexSuffixType;
    private final Class<T> clazz;
    private final EntityMapper<T> entityMapper;
    private final Document document;
    private RequestOptions options = RequestOptions.DEFAULT;

    private final AtomicInteger readConcurrency = new AtomicInteger();

    private final AtomicInteger writeConcurrency = new AtomicInteger();

    private final LongAdder readOps = new LongAdder();

    private final DoubleAdder readTimeTotal = new DoubleAdder();

    private final LongAdder writeOps = new LongAdder();

    private final DoubleAdder writeTimeTotal = new DoubleAdder();

    /**
     * 数据操作时，是否校验索引存在。
     * 开启该参数后，save时，如果不存在，会自动创建索引。
     */
    private boolean checkIndexStateOnSave = false;

    private final ConcurrentMap<String, Object> indexStateCache = new ConcurrentHashMap<>();

    private final RateLimiter saveRateLimiter = RateLimiter.create(50);

    private final RateLimiter searchRateLimiter = RateLimiter.create(300);

    public ElasticsearchTemplate(RestHighLevelClient restHighLevelClient, Class<T> clazz) {
        this(restHighLevelClient, clazz, new DefaultEntityMapper<>());
    }

    public ElasticsearchTemplate(RestHighLevelClient restHighLevelClient, Class<T> clazz,
                                 EntityMapper<T> entityMapper) {
        this.clazz = clazz;
        Document document = clazz.getAnnotation(Document.class);
        checkDocument(document);
        this.document = document;
        this.restHighLevelClient = restHighLevelClient;
        this.index = document.index();
        this.indexSuffixType = document.indexSuffixType();
        this.entityMapper = entityMapper;
    }

    public ElasticsearchTemplate<T> setSaveQps(double saveQps) {
        saveRateLimiter.setRate(saveQps);
        return this;
    }

    public ElasticsearchTemplate<T> setSearchQps(double readQps) {
        searchRateLimiter.setRate(readQps);
        return this;
    }

    public ElasticsearchTemplate<T> setCheckIndexStateOnSave(boolean checkIndexStateOnSave) {
        this.checkIndexStateOnSave = checkIndexStateOnSave;
        return this;
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

    public ElasticsearchTemplate<T> setOptions(RequestOptions options) {
        this.options = Objects.requireNonNull(options);
        return this;
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
        return StringUtils.isNotBlank(value.getDocIndex()) ? value.getDocIndex() :
                indexSuffixType.getCurrentIndex(this.index, value.getTimestampSupplier());
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
        } catch (Exception e) {
            throw EsDataException.convert(e);
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
            if (!existIndex(index)) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                //设置别名
                if (StringUtils.isNotEmpty(document.alias())) {
                    createIndexRequest.alias(new Alias(document.alias()));
                }
                //设置settings
                if (StringUtils.isEmpty(document.settingPath())) {
                    int shards = WorkRegion.getCurrentWorkRegion().isDevelopOrTest() ? 1 : document.shards();
                    Settings.Builder settings = Settings.builder()
                            .put("index.number_of_shards", shards)
                            .put("index.number_of_replicas", document.replicas());
                    if (document.max_result_window() > 0) {
                        settings.put("index.max_result_window", document.max_result_window());
                    }
                    createIndexRequest.settings(settings);
                } else {
                    createIndexRequest.settings(Util.getFileContent(document.settingPath()), XContentType.JSON);
                }
                //设置mapping
                createIndexRequest.mapping(Util.getFileContent(document.mappingPath()), XContentType.JSON);
                CreateIndexResponse createIndexResponse = indices.create(createIndexRequest, options);
                if (!createIndexResponse.isAcknowledged()) {
                    LOGGER.error("创建索引[{}]发生异常", index);
                    return false;
                }
                LOGGER.info("create index {}", index);
                return true;
            } else {
                LOGGER.info("index {} existed", index);
                return false;
            }
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.BAD_REQUEST) {
                if (existIndex(index)) {
                    return false;
                }
            }
            throw EsDataException.convert(e);
        } catch (Exception e) {
            throw EsDataException.convert(e);
        }
    }


    public int update(T value) {
        return update(value, false);
    }

    /**
     * 单条doc根据docId更新
     *
     * @param value
     * @param sync
     * @return
     */
    public int update(T value, boolean sync) {
        writeConcurrency.incrementAndGet();
        long startTime = System.nanoTime();
        String index = getCurrentIndex(value);
        UpdateRequest updateRequest = new UpdateRequest(index, value.getDocId())
                .doc(entityMapper.mapToString(value), XContentType.JSON)
                .setRefreshPolicy(sync ? WriteRequest.RefreshPolicy.WAIT_UNTIL : WriteRequest.RefreshPolicy.NONE);

        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, options);
            return (updateResponse.getResult() == DocWriteResponse.Result.UPDATED || updateResponse.getResult() == DocWriteResponse.Result.NOOP) ? 1 : 0;
        } catch (Exception e) {
            throw EsDataException.convert(e);
        } finally {
            writeConcurrency.decrementAndGet();
            time(startTime, true, "update one value");
        }
    }

    public int save(T value) {
        return save(value, false);
    }

    public int save(T value, boolean sync) {
        writeConcurrency.incrementAndGet();
        long startTime = System.nanoTime();
        String index = getCurrentIndex(value);
        checkIndexStateOnSave(index);
        IndexRequest request = new IndexRequest(index)
                .id(value.getDocId())
                .source(entityMapper.mapToString(value), XContentType.JSON)
                .setRefreshPolicy(sync ? WriteRequest.RefreshPolicy.WAIT_UNTIL : WriteRequest.RefreshPolicy.NONE)
                .opType(DocWriteRequest.OpType.INDEX);
        if (value.getPipelineSupplier() != null) {
            String pipeline = value.getPipelineSupplier().get();
            if (StringUtils.isNotEmpty(pipeline)) {
                request.setPipeline(pipeline);
            }
        }
        try {
            acquireSavePermits();
            IndexResponse indexResponse = restHighLevelClient.index(request, options);
            int status = indexResponse.status().getStatus();
            return (status >= 200 && status < 300) ? 1 : 0;
        } catch (Exception e) {
            throw EsDataException.convert(e);
        } finally {
            writeConcurrency.decrementAndGet();
            time(startTime, true, "save one value");
        }
    }

    public void save(T[] value) {
        save(Arrays.asList(value), false);
    }

    public int save(List<T> dataList, boolean sync) {
        if (dataList.size() == 1) {
            return save(dataList.get(0), sync);
        }
        // ES官方建议一个bulkRequest不要超过5MB，根据测算，大概1000条左右
        List<List<T>> lists = Lists.partition(dataList, 1024);
        int i = 0;
        for (List<T> list : lists) {
            i = i + saveOneBatch(list, sync);
        }
        return i;
    }

    private int saveOneBatch(List<T> dataList, boolean sync) {
        writeConcurrency.incrementAndGet();
        long startTime = System.nanoTime();
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(sync ? WriteRequest.RefreshPolicy.WAIT_UNTIL : WriteRequest.RefreshPolicy.NONE);
        for (T t : dataList) {
            // 如果用户自己设置了docIndex，那么使用用户设置的。否则使用框架的规则
            String index = getCurrentIndex(t);
            checkIndexStateOnSave(index);
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
            acquireSavePermits();
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
            throw EsDataException.convert(e);
        } finally {
            writeConcurrency.decrementAndGet();
            time(startTime, true, "save list value");
        }
    }

    private void acquireSavePermits() {
        long acquireStartTime = System.currentTimeMillis();
        saveRateLimiter.acquire();
        long acquireCostTime = System.currentTimeMillis() - acquireStartTime;
        if (acquireCostTime > 2_000) {
            LOGGER.warn("saveRateLimiter acquire slow, cost {} mills", acquireCostTime);
        }
    }

    private void checkIndexStateOnSave(String index) {
        if (checkIndexStateOnSave) {
            indexStateCache.computeIfAbsent(index, i -> {
                LOGGER.info("try create index {} if not exist  ", i);
                createIndex(i);
                return Boolean.TRUE;
            });
        }
    }


    public int delete(String id) {
        String index = getCurrentIndex();
        return delete(id, index);
    }

    public int delete(String id, String index) {
        writeConcurrency.incrementAndGet();
        long startTime = System.nanoTime();
        try {
            DeleteRequest deleteRequest = new DeleteRequest(index, id);
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, options);
            return deleteResponse.status() == RestStatus.OK ? 1 : 0;
        } catch (Exception e) {
            throw EsDataException.convert(e);
        } finally {
            writeConcurrency.decrementAndGet();
            time(startTime, true, "delete one value");
        }
    }


    public T get(String id) {
        return get(id, document.alias());
    }

    public T get(String id, String index) {
        long startTime = System.nanoTime();
        try {
            readConcurrency.incrementAndGet();
            GetRequest getRequest = new GetRequest(index).id(id);
            GetResponse getResponse = restHighLevelClient.get(getRequest, options);
            if (getResponse.isExists()) {
                T object = entityMapper.mapToObject(getResponse.getSourceAsString(), clazz);
                object.setDocId(id);
                object.setDocIndex(getResponse.getIndex());
                return object;
            }
            return null;
        } catch (Exception e) {
            throw EsDataException.convert(e);
        } finally {
            readConcurrency.decrementAndGet();
            time(startTime, false, "get one value");
        }
    }

    private SearchResponse search0(SearchRequest searchRequest) {
        boolean s = searchRateLimiter.tryAcquire(1, 1, TimeUnit.SECONDS);
        if (s) {
            long startTime = System.nanoTime();
            try {
                readConcurrency.incrementAndGet();
                return restHighLevelClient.search(searchRequest, options);
            } catch (Exception e) {
                throw EsDataException.convert(e);
            } finally {
                readConcurrency.decrementAndGet();
                time(startTime, false, "search value");
            }
        } else {
            throw new EsDataException(String.format("search qps is over limit[%f],aborted search after 1 second wait", searchRateLimiter.getRate()));
        }
    }


    public Pagination<T> parseSearchResponse(SearchResponse searchResponse) {
        SearchHits searchHits = searchResponse.getHits();
        TotalHits totalHits = searchHits.getTotalHits();
        List<T> resultList = new ArrayList<>(searchHits.getHits().length);
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

    private void time(long startTime, boolean isWrite, String action) {
        long timeCost = (System.nanoTime() - startTime);
        if (isWrite) {
            writeOps.increment();
            writeTimeTotal.add(timeCost / 1000_000.0);
        } else {
            readOps.increment();
            readTimeTotal.add(timeCost / 1000_000.0);
        }
        SLOW_LOGGER.mayLog(timeCost, Map.of("index", this.index, "w", isWrite, "cmd", action), "");
    }

    private int getReadConcurrency() {
        return readConcurrency.get();
    }

    private int getWriteConcurrency() {
        return writeConcurrency.get();
    }

    public long getReadOps() {
        return readOps.longValue();
    }

    public double getReadTimeTotal() {
        return readTimeTotal.doubleValue();
    }

    public long getWriteOps() {
        return writeOps.longValue();
    }

    public double getWriteTimeTotal() {
        return writeTimeTotal.doubleValue();
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("grus.elasticsearch.read.concurrency", this, ElasticsearchTemplate::getReadConcurrency).tags(Tags.of(
                        "index",
                        index))
                .baseUnit(BaseUnits.THREADS).register(registry);
        Gauge.builder("grus.elasticsearch.write.concurrency", this, ElasticsearchTemplate::getWriteConcurrency).tags(Tags.of("index",
                        index))
                .baseUnit(BaseUnits.THREADS).register(registry);

        FunctionTimer.builder("grus.elasticsearch.read.time", this, ElasticsearchTemplate::getReadOps,
                ElasticsearchTemplate::getReadTimeTotal, TimeUnit.MILLISECONDS).tags(Tags.of("index",
                index)).register(registry);
        FunctionTimer.builder("grus.elasticsearch.write.time", this, ElasticsearchTemplate::getWriteOps,
                ElasticsearchTemplate::getWriteTimeTotal, TimeUnit.MILLISECONDS).tags(Tags.of("index",
                index)).register(registry);
    }

}
