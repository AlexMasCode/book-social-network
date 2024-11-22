package com.ukma.stats.service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class ClockTestConfiguration {
        @Bean
        public Clock clock() {
            return Clock.fixed(Instant.parse("2024-11-22T00:00:00Z"), ZoneId.systemDefault());
        }
    }