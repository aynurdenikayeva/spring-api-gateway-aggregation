package com.aynur.api_gateway_service.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static class Window {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStartMs;
        Window(long now) { this.windowStartMs = now; }
    }

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private static final int LIMIT_PER_MIN = 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        String method = exchange.getRequest().getMethod() != null
                ? exchange.getRequest().getMethod().name()
                : "";

        // yalnız POST /files üçün limit
        if (!"/files".equals(path) || !"POST".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        String clientKey = getClientKey(exchange);
        long now = System.currentTimeMillis();

        Window w = windows.computeIfAbsent(clientKey, k -> new Window(now));

        if (now - w.windowStartMs >= 60_000) {
            w.windowStartMs = now;
            w.count.set(0);
        }

        int current = w.count.incrementAndGet();
        if (current > LIMIT_PER_MIN) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private String getClientKey(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = "unknown";
        return ip;
    }

    @Override
    public int getOrder() {
        return -20;
    }
}
