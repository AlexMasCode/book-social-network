package com.ukma.main.service.download.book.stats;

import com.ukma.main.service.download.book.stats.dto.DownloadBookStatsDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DownloadBookStatsService {

    DownloadBookRecordClient downloadBookRecordClient;

    public List<DownloadBookStatsDto> countBookDownloadRecordsForLastSevenDays(Long bookId) {
        List<DownloadRecord> downloadRecords = downloadBookRecordClient.findAll(bookId);
        List<DownloadBookStatsDto> resultList = new ArrayList<>();
        int dayCount = 7;

        while (dayCount-- > 0) {
            Instant requiredDate = Instant.now().minus(Duration.of(dayCount, ChronoUnit.DAYS)).truncatedTo(ChronoUnit.DAYS);
            long downloadCount = downloadRecords.stream()
                .filter(record -> record.getDownloadTime().truncatedTo(ChronoUnit.DAYS).equals(requiredDate))
                .count();
            resultList.add(createDownloadBookStatsDto(requiredDate, (int) downloadCount));
        }

        return resultList;
    }

    private DownloadBookStatsDto createDownloadBookStatsDto(Instant instant, Integer downloadCount) {
        String datePattern = "dd.MM.yyyy";
        String dateAsString = DateTimeFormatter.ofPattern(datePattern).withZone(ZoneId.systemDefault()).format(instant);

        return new DownloadBookStatsDto(
            dateAsString,
            downloadCount
        );
    }
}
