package com.aynur.api_gateway_service.exception;

import com.aynur.commonlib.error.ApiError;
import com.aynur.commonlib.error.ErrorCode;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

@Component
@Order(-1) // default handlerdən əvvəl
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Əgər response artıq yazılıbsa, burda dayandır
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        HttpStatus status = mapStatus(ex);
        ErrorCode code = mapCode(status, ex);
        String path = exchange.getRequest().getURI().getPath();
        String cid = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
        String message = buildMessage(ex, status);

        ApiError apiError = ApiError.of(status.value(), message, code, path, cid);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = """
                {"timestamp":"%s","status":%d,"message":"%s","code":"%s","path":"%s","correlationId":"%s"}
                """.formatted(apiError.timestamp(), apiError.status(),
                escape(apiError.message()), apiError.code().name(), apiError.path(),
                apiError.correlationId() == null ? "" : apiError.correlationId());

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
    private HttpStatus mapStatus(Throwable ex) {
        // Timeout / connection refused kimi hallar çox vaxt 502/503 kimi göstərilir
        String name = ex.getClass().getName().toLowerCase();
        if (name.contains("timeout")) return HttpStatus.GATEWAY_TIMEOUT;      // 504
        if (name.contains("connect") || name.contains("refused")) return HttpStatus.SERVICE_UNAVAILABLE; // 503
        return HttpStatus.BAD_GATEWAY; // 502 default
    }
    private ErrorCode mapCode(HttpStatus status, Throwable ex) {
        return switch (status) {
            case GATEWAY_TIMEOUT -> ErrorCode.TIMEOUT;
            case SERVICE_UNAVAILABLE -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> ErrorCode.BAD_GATEWAY;
        };
    }
    private String buildMessage(Throwable ex, HttpStatus status) {
        // çox uzun stack trace mesajı qaytarmırıq
        return switch (status) {
            case GATEWAY_TIMEOUT -> "Upstream service timed out";
            case SERVICE_UNAVAILABLE -> "Upstream service is unavailable";
            default -> "Bad gateway";
        };
    }
    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}

