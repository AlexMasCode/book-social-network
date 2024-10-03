package com.ukma.main.service.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtService jwtService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String bearerPrefix = "Bearer ";

        if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith(bearerPrefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(bearerPrefix.length());
        String username = jwtService.getUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && jwtService.isTokenValid(jwt)) {
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(createJwt(jwt));
            jwtAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
        } else {
            throw new RuntimeException("user is not authorized");
        }

        filterChain.doFilter(request, response);
    }

    private Jwt createJwt(String token) {
        return new Jwt(
            token,
            jwtService.getClaim(token, Claims::getIssuedAt).toInstant(),
            jwtService.getClaim(token, Claims::getExpiration).toInstant(),
            Map.of("header", "Header"),
            Map.of("claim", "Claim")
        );
    }
}
