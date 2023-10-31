/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.springservice.trace;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.service.GrusServiceHttpHeader;
import com.ciicgat.sdk.util.system.Systems;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * Created by August.Zhou on 2023/10/31 10:11
 */
public class TraceInterceptor implements ExchangeFilterFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceInterceptor.class);
    private static final TextMapSetter<ClientRequest.Builder> setter = (carrier, key, value) -> carrier.header(key, value);

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        Tracer tracer = OpenTelemetrys.getTracer();
        Span span = tracer.spanBuilder(request.method() + " " + request.url().getPath()).setSpanKind(SpanKind.CLIENT).startSpan();
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("http.method", request.method().toString());
            span.setAttribute("component", "feign");

            ClientRequest.Builder clientRequestBuilder = ClientRequest.from(request).headers(new Consumer<HttpHeaders>() {
                @Override
                public void accept(HttpHeaders httpHeaders) {
                    httpHeaders.add(GrusServiceHttpHeader.REQ_APP_NAME, Systems.APP_NAME);
                    httpHeaders.add(GrusServiceHttpHeader.REQ_APP_INSTANCE, Systems.APP_INSTANCE);
                    httpHeaders.add(GrusServiceHttpHeader.HTTP_UA_HEADER, "Grus spring service client");
                }
            });
            OpenTelemetrys.getTextMapPropagator().inject(Context.current(), clientRequestBuilder, setter);
            return next.exchange(clientRequestBuilder.build());
        } finally {
            span.end();
            SlowLogger.logEvent(Module.FEIGN, span, request.toString());
        }
    }
}
