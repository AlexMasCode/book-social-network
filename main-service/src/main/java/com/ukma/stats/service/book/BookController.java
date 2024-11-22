package com.ukma.stats.service.book;

import com.ukma.stats.service.book.dto.BookDto;
import com.ukma.stats.service.book.dto.BookListFilterDto;
import com.ukma.stats.service.book.dto.CreateBookDto;
import com.ukma.stats.service.dto.ExceptionDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BookController {

    BookService bookService;

    @PostMapping
    public ResponseEntity<?> createBook(@Valid @ModelAttribute CreateBookDto createBookDto) {
        try {
            log.info("start a process of a book creation");
            return ResponseEntity.status(HttpStatus.CREATED).body(bookService.save(createBookDto));
        } catch (Exception exception) {
            log.error("Error occurred during a process of a book creation: {}", exception.getMessage());
            return ResponseEntity.badRequest().body(new ExceptionDto(exception.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAll(@ModelAttribute BookListFilterDto bookListFilterDto) {
        return ResponseEntity.ok(bookService.getAllBooks(bookListFilterDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    @GetMapping("/{id}/download")
    public void downloadBook(
        @PathVariable Long id,
        HttpServletResponse servletResponse
    ) throws IOException {
        this.bookService.downloadBook(id, servletResponse, (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable("id") Long id) {
        bookService.deleteOne(id);
    }
}