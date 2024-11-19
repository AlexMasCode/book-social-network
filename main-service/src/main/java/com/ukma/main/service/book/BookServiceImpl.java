package com.ukma.main.service.book;

import com.ukma.main.service.protobuf.Empty;
import com.ukma.main.service.protobuf.GetUserRequest;
import com.ukma.main.service.protobuf.UserResponse;
import com.ukma.main.service.protobuf.UserServiceGrpc;
import com.ukma.main.service.user.UserClient;
import com.ukma.main.service.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl {

    BookRepository bookRepository;
    UserClient userClient;

    @GrpcClient("authentication-service")
    UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public Book save(Book book) {
        if (getAuthorViaGrpc(book.getAuthorId()) == null) {
            throw new NoSuchElementException("author with such id does not exist");
        }
        return bookRepository.save(book);

    }

    public Optional<Book> getBook(Long id) {
        return bookRepository.findById(id);
    }

    public Book updateOne(Book book, Long id) {
        return bookRepository.save(book);
    }

    public void deleteOne(Long id) {
        bookRepository.deleteById(id);
    }

    public UserDto getAuthorByFeignClient(String authorId) {
        return this.userClient.findAll().stream()
            .filter(user -> user.getId().equals(authorId))
            .findFirst()
            .orElse(null);
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

    @JmsListener(destination = "user.deletion", containerFactory = "pubSubFactory")
    public void deleteAllByAuthorId(String authorId) {
        bookRepository.deleteAllByAuthorId(authorId);
    }

    public List<UserDto> getAllUsersViaGrpc() {
        ArrayList<UserDto> users = new ArrayList<>();
        Empty request = Empty.newBuilder().build();

        userServiceBlockingStub.getAllUsers(request).forEachRemaining(user -> users.add(new UserDto(user.getId(), user.getUsername())));
        return users;
    }
}
