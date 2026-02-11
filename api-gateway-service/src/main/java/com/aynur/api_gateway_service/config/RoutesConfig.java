package com.aynur.api_gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator routeLocator(
            RouteLocatorBuilder builder,
            @Value("${services.auth}") String authUrl,
            @Value("${services.notification}") String notificationUrl,
            @Value("${services.file}") String fileUrl
    ) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri(authUrl))
                .route("notification-service", r -> r.path("/notifications/**")
                        .uri(notificationUrl))
                .route("file-service", r -> r.path("/files/**")
                        .uri(fileUrl))
                .build();
    }
}
