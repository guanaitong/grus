/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

import com.ciicgat.grus.service.discovery.ServiceDiscoveryClient;
import com.ciicgat.grus.service.discovery.ServiceInstance;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1EndpointAddress;
import io.kubernetes.client.openapi.models.V1EndpointPort;
import io.kubernetes.client.openapi.models.V1EndpointSubset;
import io.kubernetes.client.openapi.models.V1Endpoints;
import io.kubernetes.client.openapi.models.V1EndpointsList;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: August
 * @Date: 2021/7/7 19:16
 */
public class KubernetesServiceDiscoveryClient implements ServiceDiscoveryClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesServiceDiscoveryClient.class);


    private final ApiClient apiClient;
    private final SharedInformerFactory factory;
    private final String currentNamespace;
    private Lister<V1Endpoints> endpointsLister;
    private final ConcurrentMap<String, List<ServiceInstance>> cache = new ConcurrentHashMap<>();

    public KubernetesServiceDiscoveryClient() {
        this(KubernetesClientUtils.kubernetesApiClient(), KubernetesClientUtils.getCurrentNamespace());
    }

    public KubernetesServiceDiscoveryClient(ApiClient apiClient, String currentNamespace) {
        this.apiClient = apiClient;
        this.currentNamespace = currentNamespace;
        Configuration.setDefaultApiClient(apiClient);
        OkHttpClient httpClient =
                apiClient.getHttpClient().newBuilder().readTimeout(0, TimeUnit.SECONDS).build();
        apiClient.setHttpClient(httpClient);
        factory = new SharedInformerFactory();
        start();
    }

    private void start() {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        SharedIndexInformer<V1Endpoints> v1EndpointsSharedIndexInformer = factory.sharedIndexInformerFor(
                params -> coreV1Api.listNamespacedEndpointsCall(
                        currentNamespace,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        params.resourceVersion,
                        null,
                        params.timeoutSeconds,
                        params.watch,
                        null),
                V1Endpoints.class, V1EndpointsList.class);
        v1EndpointsSharedIndexInformer.addEventHandler(new ResourceEventHandler<V1Endpoints>() {
            @Override
            public void onAdd(V1Endpoints obj) {
                fireCacheChange(obj);
            }

            @Override
            public void onUpdate(V1Endpoints oldObj, V1Endpoints newObj) {
                fireCacheChange(oldObj);
                fireCacheChange(newObj);
            }

            @Override
            public void onDelete(V1Endpoints obj, boolean deletedFinalStateUnknown) {
                fireCacheChange(obj);
            }
        });
        endpointsLister = new Lister<>(v1EndpointsSharedIndexInformer.getIndexer(), currentNamespace);

        factory.startAllRegisteredInformers();
        if (!KubernetesClientUtils.poll(Duration.ofSeconds(1), Duration.ofSeconds(30),
                () -> {
                    LOGGER.info("Waiting for the cache of informers to be fully loaded..");
                    return v1EndpointsSharedIndexInformer.hasSynced();
                })) {
            throw new IllegalStateException("Timeout waiting for informers cache to be ready, is the kubernetes service up?");
        }
        LOGGER.info("Cache fully loaded (total " + endpointsLister.list().size() + " endpoints) , discovery client is now available");
//        ShutdownHook.addShutdownHook(() -> factory.stopAllRegisteredInformers());
    }

    private void fireCacheChange(V1Endpoints endpoints) {
        String name = endpoints.getMetadata().getName();
        if (cache.remove(name) != null) {
            LOGGER.info("fire service {} changed", name);
        }
    }

    @Override
    public List<ServiceInstance> getInstances(String service) {
        List<ServiceInstance> instanceList = cache.get(service);
        if (instanceList == null) {
            return cache.computeIfAbsent(service, s -> getServiceInstances(s));
        }
        return instanceList;
    }


    /**
     * lister本身就是缓存，但是因为其底层的get使用了sync同步锁，性能有问题
     *
     * @param serviceName
     * @return
     */
    private List<ServiceInstance> getServiceInstances(String serviceName) {
        V1Endpoints ep = endpointsLister.get(serviceName);
        if (ep == null || ep.getSubsets() == null) {
            return Collections.emptyList();
        }
        List<ServiceInstance> kubernetesServiceInstanceList = new ArrayList<>(4);
        for (V1EndpointSubset subset : ep.getSubsets()) {
            if (subset.getPorts() == null || subset.getPorts().size() == 0) {
                continue;
            }
            List<V1EndpointPort> endpointPorts = subset.getPorts();
            final int port = findEndpointPort(endpointPorts);

            if (!CollectionUtils.isEmpty(subset.getAddresses())) {
                for (V1EndpointAddress address : subset.getAddresses()) {
                    kubernetesServiceInstanceList.add(new ServiceInstance(address.getIp(), port));
                }
            } else if (!CollectionUtils.isEmpty(subset.getNotReadyAddresses())) {
                for (V1EndpointAddress address : subset.getNotReadyAddresses()) {
                    kubernetesServiceInstanceList.add(new ServiceInstance(address.getIp(), port));
                }
            }
        }
        return kubernetesServiceInstanceList;
    }

    private int findEndpointPort(List<V1EndpointPort> endpointPorts) {
        if (endpointPorts.size() == 1) {
            return endpointPorts.get(0).getPort();
        } else {
            for (V1EndpointPort endpointPort : endpointPorts) {
                if ("TCP".equals(endpointPort.getProtocol()) && Objects.equals(endpointPort.getPort(), 80)) {
                    return 80;
                }
            }
            for (V1EndpointPort endpointPort : endpointPorts) {
                if ("TCP".equals(endpointPort.getProtocol())) {
                    return endpointPort.getPort();
                }
            }
            return 80;
        }
    }


}
