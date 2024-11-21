package com.ukma.main.service;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MainServiceApplicationTests {

   // @Autowired
   // WebApplicationContext webApplicationContext;
//
   // @Autowired
   // ObjectMapper objectMapper;
//
   // @Autowired
   // BookRepository bookRepository;
//
   // @MockBean
   // BookServiceImpl bookService;
//
   // @MockBean
   // S3Service s3Service;
//
   // MockMvc mockMvc;
//
   // @BeforeEach
   // public void init() {
   //     bookRepository.deleteAll();
   //     mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
   // }
//
   // @Test
   // void testThatBookCreatedSuccessfully() throws Exception {
   //     Book book = new Book(
   //         1L,
   //         "Book!",
   //         "some content",
   //         "authorId1",
   //         "coverImageName1",
   //         List.of()
   //     );
//
   //     when(bookService.save(any(Book.class))).thenReturn(book);
//
   //     mockMvc.perform(
   //             post("/api/books")
   //                 .contentType(MediaType.APPLICATION_JSON)
   //                 .content(objectMapper.writeValueAsString(book))
   //         )
   //         .andExpect(status().isCreated())
   //         .andExpect(jsonPath("$.title").value(book.getTitle()))
   //         .andExpect(jsonPath("$.content").value(book.getContent()))
   //         .andExpect(jsonPath("$.authorId").value(book.getAuthorId()))
   //         .andExpect(jsonPath("$.coverImageName").value(book.getCoverImageName()));
//
   //     verify(bookService, times(1)).save(any(Book.class));
   // }
//
   // @Test
   // void testThatBookFailsWithBadRequestStatus_whenUserIsNotFound() throws Exception {
   //     Book book = new Book(
   //         1L,
   //         "Book!",
   //         "some content",
   //         "",
   //         "coverImageName1",
   //         List.of()
   //     );
//
   //     when(bookService.save(any(Book.class))).thenThrow(NoSuchElementException.class);
//
   //     mockMvc.perform(
   //             post("/api/books")
   //                 .contentType(MediaType.APPLICATION_JSON)
   //                 .content(objectMapper.writeValueAsString(book))
   //         )
   //         .andExpect(status().isBadRequest());
   // }
//
   ////@Test
   ////void testThatImageUploadIsSuccessful() throws Exception {
   ////    Book book = new Book(
   ////        1L,
   ////        "Book!",
   ////        "some content",
   ////        "",
   ////        "coverImageName1",
   ////        List.of()
   ////    );
   ////    MockMultipartFile mockMultipartImage = new MockMultipartFile(
   ////        "file",
   //        "image.png",
   //        MediaType.IMAGE_PNG_VALUE,
   //        new byte[]{}
   //    );

   //    when(bookService.getBook(any(Long.class))).thenReturn(Optional.of(book));

   //    mockMvc.perform(
   //            multipart("/api/books/%d/cover-image".formatted(1L))
   //                .file(mockMultipartImage)
   //        )
   //        .andExpect(status().isOk())
   //        .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));

   //    verify(bookService, times(1)).getBook(any(Long.class));
   //}


   //@Test
   //void testThatImageUploadFailsIfFileTypeIsOctetStream() throws Exception {
   //    MockMultipartFile mockMultipartImage = new MockMultipartFile(
   //        "file",
   //        "some file.xlsx",
   //        MediaType.APPLICATION_OCTET_STREAM_VALUE,
   //        new byte[]{}
   //    );

   //    mockMvc.perform(
   //            multipart("/api/books/1/cover-image")
   //                .file(mockMultipartImage)
   //        )
   //        .andExpect(status().isBadRequest());
   //}

   //@Test
   //void testThatImageUploadFailsIfFileTypeIsTextHtml() throws Exception {
   //    MockMultipartFile mockMultipartImage = new MockMultipartFile(
   //        "file",
   //        "some file.xlsx",
   //        MediaType.TEXT_HTML_VALUE,
   //        new byte[]{}
   //    );

   //    mockMvc.perform(
   //            multipart("/api/books/1/cover-image")
   //                .file(mockMultipartImage)
   //        )
   //        .andExpect(status().isBadRequest());
   //}

}
