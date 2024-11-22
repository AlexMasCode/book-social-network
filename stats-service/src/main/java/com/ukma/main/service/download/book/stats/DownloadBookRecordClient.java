package com.ukma.main.service.download.book.stats;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "main-service", url = "http://localhost:8072")
public interface DownloadBookRecordClient {

    @GetMapping("/api/download-records/{bookId}")
    List<DownloadRecord> findAll(@PathVariable Long bookId);
}
