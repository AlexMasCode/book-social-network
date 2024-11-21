package com.ukma.main.service.download.records;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/download-records")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DownloadRecordController {

    DownloadRecordService downloadRecordService;

    @GetMapping("/{bookId}")
    public List<DownloadRecord> findAll(@PathVariable Long bookId) {
        return downloadRecordService.findAll(bookId);
    }
}
