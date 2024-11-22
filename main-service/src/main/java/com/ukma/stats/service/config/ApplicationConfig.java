package com.ukma.stats.service.config;

import com.cloudinary.Cloudinary;
import com.ukma.stats.service.protobuf.UserServiceGrpc;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

@Configuration
public class ApplicationConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public ManagedChannel managedChannel(DiscoveryClient discoveryClient) {
        String authenticationServiceName = "authentication-service";
        ServiceInstance address = discoveryClient.getInstances(authenticationServiceName)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Service not found: " + authenticationServiceName));
        return ManagedChannelBuilder.forAddress(address.getHost(), address.getPort()).usePlaintext().build();
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(ManagedChannel channel) {
        return UserServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public CallCredentials bearerAuthForwardingCredentials() {
        return CallCredentialsHelper.bearerAuth(() -> ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken().getTokenValue());
    }

    @Bean
    public Cloudinary cloudinary(
        @Value("${cloudinary.cloudName}") String cloudName,
        @Value("${cloudinary.apiKey}") String apiKey,
        @Value("${cloudinary.apiSecret}") String apiSecret
    ) {
        return new Cloudinary(
            Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
            )
        );
    }
}
