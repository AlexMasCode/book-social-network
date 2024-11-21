package com.ukma.main.service.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ukma.main.service.comments.Comment;
import com.ukma.main.service.download.records.DownloadRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String description;
    String genre;
    String authorId;
    String publicUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    List<DownloadRecord> downloadRecords = new ArrayList<>();
}
