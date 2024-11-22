package com.ukma.stats.service.download.records;

import com.ukma.stats.service.download.records.dto.DownloadRecordDto;
import lombok.AccessLevel;
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
    public List<DownloadRecordDto> findAll(@PathVariable Long bookId) {
        return downloadRecordService.findAll(bookId);
    }
}
