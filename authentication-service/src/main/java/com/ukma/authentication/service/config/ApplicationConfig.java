package com.ukma.authentication.service.config;

import com.ukma.authentication.service.auth.JwtService;
import com.ukma.authentication.service.user.User;
import com.ukma.authentication.service.user.UserService;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ApplicationConfig {

    UserService userService;
    JwtService jwtService;

    @Bean
    public UserDetailsService userDetailsService() {
        return userService::findByUsername;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new BasicGrpcAuthenticationReader();
    }

    @Bean
    public GrpcAuthenticationReader authenticationReader() {
        final List<GrpcAuthenticationReader> readers = new ArrayList<>();
        readers.add(new BearerAuthenticationReader(this::authenticationFunction));
        return new CompositeGrpcAuthenticationReader(readers);
    }

    public Authentication authenticationFunction(String jwt) {
        Claims claims = jwtService.extractAllClaims(jwt);
        User user = userService.findByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            user.getPassword()
        );
    }
}
