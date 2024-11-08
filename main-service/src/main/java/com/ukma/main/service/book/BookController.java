package com.ukma.main.service.book;

import com.ukma.main.service.aws.S3Service;
import com.ukma.main.service.dto.ExceptionDto;
import com.ukma.main.service.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BookController {

    S3Service s3Service;
    BookService bookService;

    @PostMapping("")
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            log.info("start a process of a book creation");
            return ResponseEntity.status(HttpStatus.CREATED).body(bookService.save(book));
        } catch (Exception exception) {
            log.error("Error occurred during a process of a book creation: {}", exception.getMessage());
            return ResponseEntity.badRequest().body(new ExceptionDto(exception.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Book> updateOne(@RequestBody Book book, @PathVariable Long id) {
        return ResponseEntity.ok(bookService.updateOne(book, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBook(id).orElseThrow(() -> new NoSuchElementException("book with id " + id + " does not exist")));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable("id") Long id) {
        bookService.deleteOne(id);
    }

    @GetMapping("/{bookId}/author")
    public ResponseEntity<UserDto> getBookAuthor(@PathVariable Long bookId) {
        Book book = bookService.getBook(bookId).orElseThrow(() -> new NoSuchElementException("book with id " + bookId + " does not exist"));
        UserDto userDto = bookService.getAuthorViaGrpc(book.getAuthorId());
        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userDto);
    }

    @PostMapping(value = "/{id}/cover-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCoverImage(@PathVariable Long id, @RequestPart(value = "file") MultipartFile image) throws IOException {
        String contentType = image.getContentType();
        if (contentType == null || (!contentType.equals(MediaType.IMAGE_JPEG_VALUE) &&
                                    !contentType.equals(MediaType.IMAGE_PNG_VALUE))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File has invalid extension, only jpeg and png allowed");
        }

        Book book = bookService.getBook(id).orElseThrow(() -> new NoSuchElementException("book with id " + id + " does not exist"));

        String extension = image.getOriginalFilename().split("\\.")[1];
        String newImageName = UUID.randomUUID() + "." + extension;

        s3Service.uploadImage(newImageName, image.getInputStream(), image.getSize());

        book.setCoverImageName(newImageName);
        bookService.save(book);

        return ResponseEntity.ok(newImageName);
    }

    @GetMapping(value = "/{id}/cover-image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable Long id) {
        Book book = bookService.getBook(id).orElseThrow(() -> new NoSuchElementException("book with id " + id + " does not exist"));
        String coverImageName = book.getCoverImageName();

        InputStream fileStream = s3Service.downloadImage(book.getCoverImageName());

        String mimeType = URLConnection.guessContentTypeFromName(coverImageName);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .body(new InputStreamResource(fileStream));
    }


}