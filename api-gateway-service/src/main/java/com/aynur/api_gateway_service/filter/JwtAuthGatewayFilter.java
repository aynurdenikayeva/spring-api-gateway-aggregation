package com.aynur.api_gateway_service.filter;

import com.aynur.commonlib.error.ApiError;
import com.aynur.commonlib.error.ErrorCode;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthGatewayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // Public endpoints
        if (path.startsWith("/auth/") || path.startsWith("/actuator/") || path.equals("/ping")) {
            return chain.filter(exchange);
        }
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (auth == null || !auth.startsWith("Bearer ")) {
            return writeJsonError(exchange, HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header",
                    ErrorCode.UNAUTHORIZED);
        }
        // Minimal check: "Bearer "dan sonra token boş olmasın
        String token = auth.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            return writeJsonError(exchange, HttpStatus.UNAUTHORIZED,
                    "Empty token",
                    ErrorCode.UNAUTHORIZED);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> writeJsonError(ServerWebExchange exchange, HttpStatus status, String message, ErrorCode code) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String path = exchange.getRequest().getURI().getPath();
        String cid = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");

        ApiError error = ApiError.of(status.value(), message, code, path, cid);
        // Sadə JSON yazırıq (manual). İstəsən ObjectMapper-lə də yazarıq.
        String json = """
                {"timestamp":"%s","status":%d,"message":"%s","code":"%s","path":"%s","correlationId":"%s"}
                """.formatted(error.timestamp(), error.status(),
                escape(message), code.name(), path, cid == null ? "" : cid);
        byte[] bytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    @Override
    public int getOrder() {
        return -30; // loggingdən əvvəl
    }
}
