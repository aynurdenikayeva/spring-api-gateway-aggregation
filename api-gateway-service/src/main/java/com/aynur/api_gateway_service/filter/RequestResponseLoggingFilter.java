package com.aynur.api_gateway_service.filter;

import com.aynur.commonlib.constants.HeaderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestResponseLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        long start = System.currentTimeMillis();

        String method = exchange.getRequest().getMethod() != null
                ? exchange.getRequest().getMethod().name()
                : "";

        String path = exchange.getRequest().getURI().getPath();
        String cid = exchange.getRequest().getHeaders().getFirst(HeaderConstants.CORRELATION_ID);

        return chain.filter(exchange)
                .doOnSuccess(v -> {
                    int status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;
                    long duration = System.currentTimeMillis() - start;

                    log.info("cid={} {} {} -> status={} durationMs={}", cid, method, path, status, duration);
                });
    }

    @Override
    public int getOrder() {
        return -50;
    }
}
