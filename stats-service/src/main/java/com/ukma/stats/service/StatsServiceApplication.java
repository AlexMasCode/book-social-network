package com.ukma.main.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StatsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatsServiceApplication.class, args);
    }

}
