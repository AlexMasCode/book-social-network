package com.ukma.main.service.book;

import com.ukma.main.service.book.dto.BookDto;
import com.ukma.main.service.book.dto.BookListFilterDto;
import com.ukma.main.service.book.dto.CreateBookDto;
import com.ukma.main.service.protobuf.GetUserRequest;
import com.ukma.main.service.protobuf.UserResponse;
import com.ukma.main.service.protobuf.UserServiceGrpc;
import com.ukma.main.service.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl {

    BookRepository bookRepository;
    CloudinaryService cloudinaryService;

    @GrpcClient("authentication-service")
    UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public BookDto save(CreateBookDto createBookDto) throws IOException {
        if (getAuthorViaGrpc(createBookDto.getAuthorId()) == null) {
            throw new NoSuchElementException("author with such id does not exist");
        }
        Book newBook = new Book();

        newBook.setTitle(createBookDto.getTitle());
        newBook.setDescription(createBookDto.getDescription());
        newBook.setAuthorId(createBookDto.getAuthorId());
        newBook.setGenre(createBookDto.getGenre());
        newBook.setAuthorId(createBookDto.getAuthorId());
        newBook.setPublicUrl(savePdfFile(createBookDto.getFile()));

        return convertToDto(bookRepository.save(newBook));

    }

    public BookDto getBook(Long id) {
        return bookRepository.findById(id).map(this::convertToDto).orElseThrow();
    }

    public List<BookDto> getAllBooks(BookListFilterDto bookListFilterDto) {
        return bookRepository.findAll(createFilter(bookListFilterDto))
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    public void deleteOne(Long id) {
        bookRepository.deleteById(id);
    }

    public UserDto getAuthorViaGrpc(String authorId) {
        GetUserRequest request = GetUserRequest.newBuilder()
            .setUserId(authorId)
            .build();
        UserResponse response = userServiceBlockingStub.getUserById(request);
        if (response != null && !response.getId().isEmpty()) {
            return new UserDto(response.getId(), response.getUsername());
        } else {
            return null;
        }
    }

    private Specification<Book> createFilter(BookListFilterDto listFilterDto) {
        if (listFilterDto == null) {
            return Specification.anyOf();
        }

        List<Specification<Book>> specifications = new ArrayList<>();
        if (listFilterDto.getAuthorId() != null) {
            specifications.add((
                (root, query, cb) -> cb.equal(root.get("authorId"), listFilterDto.getAuthorId())
            ));
        }
        if (listFilterDto.getGenre() != null) {
            specifications.add((
                (root, query, cb) -> cb.like(root.get("genre"), listFilterDto.getGenre())
            ));
        }

        return Specification.allOf(specifications);
    }

    private BookDto convertToDto(Book book) {
        return new BookDto(
            book.getId(),
            book.getTitle(),
            book.getDescription(),
            book.getGenre(),
            book.getAuthorId(),
            book.getPublicUrl()
        );
    }

    private String savePdfFile(MultipartFile multipartFile) throws IOException {
        if (MediaType.APPLICATION_PDF_VALUE.equals(multipartFile.getContentType())) {
            return cloudinaryService.upload(multipartFile, "books");
        } else {
            throw new IllegalArgumentException("file should have pdf format!");
        }
    }

    @JmsListener(destination = "user.deletion", containerFactory = "pubSubFactory")
    public void deleteAllByAuthorId(String authorId) {
        bookRepository.deleteAllByAuthorId(authorId);
    }
}
