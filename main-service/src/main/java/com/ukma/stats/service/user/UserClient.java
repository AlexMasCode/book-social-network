package com.ukma.stats.service.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("authentication-service")
@Component
public interface UserClient {

    @GetMapping("/api/users")
    List<UserDto> findAll();
}
