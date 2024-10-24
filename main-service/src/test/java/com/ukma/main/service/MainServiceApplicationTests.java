package com.ukma.main.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.main.service.aws.S3Service;
import com.ukma.main.service.book.Book;
import com.ukma.main.service.book.BookRepository;
import com.ukma.main.service.book.BookService;
import com.ukma.main.service.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MainServiceApplicationTests {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BookRepository bookRepository;

    @MockBean
    BookService bookService;

    @MockBean
    S3Service s3Service;

    MockMvc mockMvc;

    @BeforeEach
    public void init() {
        bookRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testThatBookCreatedSuccessfully() throws Exception {
        Book book = new Book(
            1L,
            "Book!",
            "some content",
            "authorId1",
            "coverImageName1",
            List.of()
        );

        when(bookService.save(any(Book.class))).thenReturn(book);
        when(bookService.getAuthor(any(String.class))).thenReturn(new UserDto("2ff", "Vova"));

        mockMvc.perform(
                post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(book.getTitle()))
            .andExpect(jsonPath("$.content").value(book.getContent()))
            .andExpect(jsonPath("$.authorId").value(book.getAuthorId()))
            .andExpect(jsonPath("$.coverImageName").value(book.getCoverImageName()));

        verify(bookService, times(1)).save(any(Book.class));
    }

    @Test
    void testThatBookFailsWithBadRequestStatus_whenUserIsNotFound() throws Exception {
        Book book = new Book(
            1L,
            "Book!",
            "some content",
            "",
            "coverImageName1",
            List.of()
        );

        when(bookService.save(any(Book.class))).thenThrow(NoSuchElementException.class);

        mockMvc.perform(
                post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void testThatImageUploadIsSuccessful() throws Exception {
        Book book = new Book(
            1L,
            "Book!",
            "some content",
            "",
            "coverImageName1",
            List.of()
        );
        MockMultipartFile mockMultipartImage = new MockMultipartFile(
            "file",
            "image.png",
            MediaType.IMAGE_PNG_VALUE,
            new byte[]{}
        );

        when(bookService.getBook(any(Long.class))).thenReturn(Optional.of(book));

        mockMvc.perform(
                multipart("/api/books/%d/cover-image".formatted(1L))
                    .file(mockMultipartImage)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));

        verify(bookService, times(1)).getBook(any(Long.class));
    }


    @Test
    void testThatImageUploadFailsIfFileTypeIsOctetStream() throws Exception {
        MockMultipartFile mockMultipartImage = new MockMultipartFile(
            "file",
            "some file.xlsx",
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            new byte[]{}
        );

        mockMvc.perform(
                multipart("/api/books/1/cover-image")
                    .file(mockMultipartImage)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void testThatImageUploadFailsIfFileTypeIsTextHtml() throws Exception {
        MockMultipartFile mockMultipartImage = new MockMultipartFile(
            "file",
            "some file.xlsx",
            MediaType.TEXT_HTML_VALUE,
            new byte[]{}
        );

        mockMvc.perform(
                multipart("/api/books/1/cover-image")
                    .file(mockMultipartImage)
            )
            .andExpect(status().isBadRequest());
    }

}
