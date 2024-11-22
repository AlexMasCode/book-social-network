package com.ukma.stats.service.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateBookDto {

    @NotBlank
    String title;

    @NotBlank
    String description;

    @NotBlank
    String genre;

    @NotBlank
    String authorId;

    @NotNull
    MultipartFile file;
}
