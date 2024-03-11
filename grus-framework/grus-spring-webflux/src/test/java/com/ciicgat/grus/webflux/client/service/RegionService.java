/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.webflux.client.service;

import com.ciicgat.api.core.annotation.ServiceName;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Created by August.Zhou on 2023/10/31 10:15
 */
@HttpExchange("/test")
@ServiceName("region-1")
public interface RegionService {

    @GetExchange("/getRegionById")
    Region getRegionById(@RequestParam("id") Integer id);

    // 新响应式写法，返回结果为单个
    @GetExchange("/getRegionById")
    Mono<Region> getRegionById1(@RequestParam("id") Integer id);

    @GetExchange("/getAllRegion")
    List<Region> getAllRegion();

    // 新响应式写法，返回结果为多个
    @GetExchange("/getAllRegion")
    Flux<Region> getAllRegion2();

    @PostExchange("/update")
    void updateRegion(@RequestBody Region region);
}
