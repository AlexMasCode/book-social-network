package com.ukma.main.service.book;

import com.ukma.main.service.user.UserClient;
import com.ukma.main.service.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BookService {

    BookRepository bookRepository;
    UserClient userClient;

    public Book save(Book book) {
        if (getAuthor(book.getAuthorId()) == null) {
            throw new NoSuchElementException("author with such id does not exist");
        }
        return bookRepository.save(book);

    }

    public Optional<Book> getBook(Long id){
        return bookRepository.findById(id);
    }

    public Book updateOne(Book book, Long id) {
        return bookRepository.save(book);
    }

    public void deleteOne(Long id) {
        bookRepository.deleteById(id);
    }

    public UserDto getAuthor(String authorId) {
        return this.userClient.findAll().stream()
            .filter(user -> user.getId().equals(authorId))
            .findFirst()
            .orElse(null);
    }

}
