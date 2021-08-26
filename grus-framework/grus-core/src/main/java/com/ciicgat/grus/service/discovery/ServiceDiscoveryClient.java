/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.discovery;

import java.util.Collections;
import java.util.List;

/**
 * @Author: August
 * @Date: 2021/7/9 17:58
 */
public interface ServiceDiscoveryClient {

    List<ServiceInstance> getInstances(String service);

    ServiceDiscoveryClient NOOP = service -> Collections.emptyList();
}
