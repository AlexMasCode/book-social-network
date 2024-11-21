package com.ukma.main.service.download.records.dto;

import com.ukma.main.service.book.Book;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DownloadRecordDto {

    Long id;
    Book book;
    Instant downloadTime;
    String userId;
}
