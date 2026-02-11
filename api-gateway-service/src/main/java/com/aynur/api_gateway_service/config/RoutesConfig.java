package com.aynur.api_gateway_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class RoutesConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, GatewayProperties p) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**").uri(p.getAuth()))
                .route("profile-service", r -> r.path("/profiles/**").uri(p.getProfile()))
                .route("notification-service", r -> r.path("/notifications/**").uri(p.getNotification()))
                .route("file-service", r -> r.path("/files/**").uri(p.getFile()))
                .build();
    }
}
