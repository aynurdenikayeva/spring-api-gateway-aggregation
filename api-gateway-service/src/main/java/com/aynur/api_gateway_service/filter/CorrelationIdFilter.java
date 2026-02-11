package com.aynur.api_gateway_service.filter;

import com.aynur.commonlib.constants.HeaderConstants;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String cid = exchange.getRequest().getHeaders().getFirst(HeaderConstants.CORRELATION_ID);
        if (cid == null || cid.isBlank()) {
            cid = UUID.randomUUID().toString();
        }
        // Request-ə header əlavə et (downstream üçün)
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HeaderConstants.CORRELATION_ID, cid)
                .build();

        // Response də qoy (client görsün)
        exchange.getResponse().getHeaders().set(HeaderConstants.CORRELATION_ID, cid);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
    @Override
    public int getOrder() {
        return -100;
    }
}

