package com.ukma.api.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/api/users/**", "/api/auth/**")
                        .uri("lb://authentication-service"))
                .route(p -> p.path("/api/books/**", "/api/comments/**", "/api/comment-complaints/**")
                        .uri("lb://main-service"))
                .route(p -> p.path("/actuator/auth-metrics/**")
                        .filters(f -> f.rewritePath("/authentication-service/(?<remaining>.*)", "/prometheus/${remaining}"))
                        .uri("lb://authentication-service"))
                .route(p -> p.path("/actuator/main-metrics/**")
                        .filters(f -> f.rewritePath("/main-service/(?<remaining>.*)", "/prometheus/${remaining}"))
                        .uri("lb://main-service"))
                .build();
    }
}