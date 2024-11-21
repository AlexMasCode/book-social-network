package com.ukma.main.service.download.book.stats;

import com.ukma.main.service.download.book.stats.dto.DownloadBookStatsDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book-download-records-stats")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DownloadRecordStatsController {

    DownloadBookStatsService downloadBookStatsService;

    @GetMapping("/{bookId}")
    public List<DownloadBookStatsDto> countBookDownloadRecordsForLastSevenDays(@PathVariable Long bookId) {
        return downloadBookStatsService.countBookDownloadRecordsForLastSevenDays(bookId);
    }
}
