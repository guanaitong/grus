/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.grus.service.GrusServiceHttpHeader;
import com.ciicgat.sdk.util.system.Systems;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ciicgat.api.core.FeignHttpClient.READ_TIMEOUT_TAG;

/**
 * Created by August.Zhou on 2019-08-13 11:26.
 */
public class JdkHttpClientWrapper implements Client {

    private final HttpClient client;

    public JdkHttpClientWrapper() {
        this(HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .version(HttpClient.Version.HTTP_1_1)
                .build());
    }

    public JdkHttpClientWrapper(HttpClient client) {
        this.client = Util.checkNotNull(client, "HttpClient must not be null");
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        final HttpRequest httpRequest = newRequestBuilder(request, options).build();

        HttpResponse<byte[]> httpResponse;
        try {
            httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        } catch (final InterruptedException e) { //NOSONAR
            throw new IOException("Invalid uri " + request.url(), e);
        }

        final OptionalLong length = httpResponse.headers().firstValueAsLong("Content-Length");

        final Response response = Response.builder()
                .body(new ByteArrayInputStream(httpResponse.body()),
                        length.isPresent() ? (int) length.getAsLong() : null)
                .reason(httpResponse.headers().firstValue("Reason-Phrase").orElse("OK"))
                .request(request)
                .status(httpResponse.statusCode())
                .headers(castMapCollectType(httpResponse.headers().map()))
                .build();
        return response;
    }

    private HttpRequest.Builder newRequestBuilder(Request request, Request.Options options) throws IOException {
        long readTimeoutMillis = 60000L;
        if (options.connectTimeoutMillis() != -1 && options.readTimeoutMillis() != -1) {
            readTimeoutMillis = options.readTimeoutMillis();
        } else {
            Map<String, Collection<String>> headers = request.headers();
            Collection<String> stringCollection = headers.get(READ_TIMEOUT_TAG);
            if (Objects.nonNull(stringCollection) && !stringCollection.isEmpty()) {
                readTimeoutMillis = Integer.parseInt(stringCollection.stream().findAny().orElse("60000"));
            }
        }

        URI uri;
        try {
            uri = new URI(request.url());
        } catch (final URISyntaxException e) {
            throw new IOException("Invalid uri " + request.url(), e);
        }

        final HttpRequest.BodyPublisher body;
        if (request.requestBody().asBytes() == null) {
            body = HttpRequest.BodyPublishers.noBody();
        } else {
            body = HttpRequest.BodyPublishers.ofByteArray(request.requestBody().asBytes());
        }

        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofMillis(readTimeoutMillis))
                .version(HttpClient.Version.HTTP_1_1);

        final Map<String, Collection<String>> headers = filterRestrictedHeaders(request.headers());
        if (!headers.isEmpty()) {
            requestBuilder.headers(asString(headers));
        }

        requestBuilder.headers(GrusServiceHttpHeader.REQ_APP_NAME, Systems.APP_NAME);
        requestBuilder.headers(GrusServiceHttpHeader.REQ_APP_INSTANCE, Systems.APP_INSTANCE);
        requestBuilder.headers(GrusServiceHttpHeader.HTTP_UA_HEADER, "Grus service client");

        switch (request.httpMethod()) {
            case GET:
                return requestBuilder.GET();
            case POST:
                return requestBuilder.POST(body);
            case PUT:
                return requestBuilder.PUT(body);
            case DELETE:
                return requestBuilder.DELETE();
            default:
                // fall back scenario, http implementations may restrict some methods
                return requestBuilder.method(request.httpMethod().toString(), body);
        }

    }

    /**
     * There is a bunch o headers that the http2 client do not allow to be set.
     *
     * @see jdk.internal.net.http.common.Utils.DISALLOWED_HEADERS_SET
     */
    private static final Set<String> DISALLOWED_HEADERS_SET;

    static {
        // A case insensitive TreeSet of strings.
        final TreeSet<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        treeSet.addAll(Set.of("connection", "content-length", "date", "expect", "from", "host",
                "origin", "referer", "upgrade", "via", "warning"));
        DISALLOWED_HEADERS_SET = Collections.unmodifiableSet(treeSet);
    }

    private Map<String, Collection<String>> filterRestrictedHeaders(Map<String, Collection<String>> headers) {
        final Map<String, Collection<String>> filteredHeaders = headers.keySet()
                .stream()
                .filter(headerName -> !DISALLOWED_HEADERS_SET.contains(headerName))
                .collect(Collectors.toMap(
                        Function.identity(),
                        headers::get));

        filteredHeaders.computeIfAbsent("Accept", key -> List.of("*/*"));

        return filteredHeaders;
    }

    private Map<String, Collection<String>> castMapCollectType(Map<String, List<String>> map) {
        final Map<String, Collection<String>> result = new HashMap<>();
        map.forEach((key, value) -> result.put(key, new HashSet<>(value)));
        return result;
    }

    private String[] asString(Map<String, Collection<String>> headers) {
        return headers.entrySet().stream()
                .flatMap(entry -> entry.getValue()
                        .stream()
                        .map(value -> Arrays.asList(entry.getKey(), value))
                        .flatMap(List::stream))
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }
}
