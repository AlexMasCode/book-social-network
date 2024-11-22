package com.ukma.stats.service.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class ForwardingBearerRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            template.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwtAuth.getToken().getTokenValue()));
        }
    }
}