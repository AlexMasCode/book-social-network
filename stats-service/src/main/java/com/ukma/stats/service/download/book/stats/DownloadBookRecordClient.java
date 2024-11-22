package com.ukma.stats.service.download.book.stats;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("main-service")
public interface DownloadBookRecordClient {

    @GetMapping("/api/download-records/{bookId}")
    List<com.ukma.main.service.download.book.stats.DownloadRecord> findAll(@PathVariable Long bookId);
}
