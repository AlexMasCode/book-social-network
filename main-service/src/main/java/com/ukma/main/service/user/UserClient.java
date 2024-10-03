package com.ukma.main.service.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("authentication-service")
@Component
public interface UserClient {

    @GetMapping("/api/users")
    List<UserDto> findAll();
}
