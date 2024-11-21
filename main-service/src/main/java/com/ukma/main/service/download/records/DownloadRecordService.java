package com.ukma.main.service.download.records;

import com.ukma.main.service.book.BookService;
import com.ukma.main.service.download.records.dto.DownloadRecordDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DownloadRecordService {

    BookService bookService;
    DownloadRecordRepository downloadRecordRepository;

    public void save(DownloadRecord downloadRecord) {
        downloadRecordRepository.save(downloadRecord);
    }

    public List<DownloadRecordDto> findAll(Long bookId) {
        bookService.getBook(bookId);
        return downloadRecordRepository.findAll((
                (root, query, cb) -> cb.equal(root.get("book").get("id"), bookId)
            ))
            .stream()
            .map(this::toDto)
            .toList();
    }

    private DownloadRecordDto toDto(DownloadRecord downloadRecord) {
        return new DownloadRecordDto(
            downloadRecord.getId(),
            downloadRecord.getBook().getId(),
            downloadRecord.getDownloadTime(),
            downloadRecord.getUserId()
        );
    }
}
