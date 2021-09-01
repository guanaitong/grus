/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.ClientBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static io.kubernetes.client.util.Config.ENV_SERVICE_HOST;
import static io.kubernetes.client.util.Config.ENV_SERVICE_PORT;
import static io.kubernetes.client.util.Config.SERVICEACCOUNT_NAMESPACE_PATH;

/**
 * @Author: August
 * @Date: 2021/7/8 16:07
 */
public class KubernetesClientUtils {

    private static final Log LOG = LogFactory.getLog(KubernetesClientUtils.class);

    private KubernetesClientUtils() {
    }

    public static ApiClient kubernetesApiClient() {
        try {
            // Assume we are running in a cluster
            ApiClient apiClient = ClientBuilder.cluster().build();
            if (testClient(apiClient)) {
                LOG.info("Created API client in the cluster.");
                return apiClient;
            }
            apiClient = apiClientFromConfig();
            if (testClient(apiClient)) {
                LOG.info("Create API client from config");
                return apiClient;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Create API client failed");
    }

    private static ApiClient apiClientFromConfig() throws Exception {
        final ClientBuilder builder = new ClientBuilder();
        final String host = System.getenv(ENV_SERVICE_HOST);
        final String port = System.getenv(ENV_SERVICE_PORT);
        URI uri = new URI("https", null, host, Integer.parseInt(port), null, null, null);
        builder.setBasePath(uri.toString());
        builder.setVerifyingSsl(false);
        builder.setAuthentication(new GconfAccessTokenAuthentication());
        return builder.build();
    }


    private static boolean testClient(ApiClient apiClient) {
        try {
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            coreV1Api.listNamespacedEndpoints(
                    getCurrentNamespace(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            return true;
        } catch (ApiException e) {
            LOG.warn("test failed", e);
        }
        return false;
    }

    public static String getCurrentNamespace() {
        try {
            String namespace = Files.readString(Paths.get(SERVICEACCOUNT_NAMESPACE_PATH), StandardCharsets.UTF_8);
            LOG.info("currentNamespace is " + namespace);
            return namespace;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Poll tries a condition func until it returns true, an exception, or the timeout is reached.
     *
     * @param interval  the check interval
     * @param timeout   the timeout period
     * @param condition the condition func
     */
    public static boolean poll(Duration interval, Duration timeout, Supplier<Boolean> condition) {
        return poll(interval, interval, timeout, condition);
    }

    /**
     * Poll tries a condition func until w/ the initial delay specified.
     *
     * @param initialDelay the initial delay
     * @param interval     the check interval
     * @param timeout      the timeout period
     * @param condition    the condition
     * @return returns true if gracefully finished
     */
    public static boolean poll(
            Duration initialDelay, Duration interval, Duration timeout, Supplier<Boolean> condition) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        AtomicBoolean result = new AtomicBoolean(false);
        long dueDate = System.currentTimeMillis() + timeout.toMillis();
        ScheduledFuture<?> future =
                executorService.scheduleAtFixedRate(
                        () -> {
                            try {
                                result.set(condition.get());
                            } catch (Exception e) {
                                result.set(false);
                            }
                        },
                        initialDelay.toMillis(),
                        interval.toMillis(),
                        TimeUnit.MILLISECONDS);
        try {
            while (System.currentTimeMillis() < dueDate) {
                if (result.get()) {
                    future.cancel(true);
                    return true;
                }
            }
        } catch (Exception e) {
            return result.get();
        }
        future.cancel(true);
        return result.get();
    }

}
