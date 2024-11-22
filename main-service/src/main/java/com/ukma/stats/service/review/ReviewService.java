package com.ukma.stats.service.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.stats.service.book.Book;
import com.ukma.stats.service.book.BookRepository;
import com.ukma.stats.service.book.BookService;
import com.ukma.stats.service.review.dto.CreateReviewDto;
import com.ukma.stats.service.review.dto.ReviewMailMessageDto;
import com.ukma.stats.service.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReviewService {

    ReviewRepository reviewRepository;
    BookService bookService;
    BookRepository bookRepository;
    JmsTemplate jmsTemplate;
    ObjectMapper objectMapper;

    public void save(CreateReviewDto createReviewDto, JwtAuthenticationToken jwtAuthenticationToken) throws JsonProcessingException {
        Book book = bookRepository.findById(createReviewDto.getBookId()).orElseThrow();
        UserDto reviewAuthor = bookService.getAuthorViaGrpc(jwtAuthenticationToken.getToken().getClaimAsString("userId"));
        UserDto bookAuthor = bookService.getAuthorViaGrpc(book.getAuthorId());

        Review newReview = new Review(
            createReviewDto.getTitle(),
            createReviewDto.getContent(),
            reviewAuthor.getId(),
            book
        );

        reviewRepository.saveAndFlush(newReview);

        ReviewMailMessageDto messageDto = ReviewMailMessageDto
            .builder()
            .title("New review on your book!")
            .content("Hello! User '%s' left review on your book '%s'!".formatted(bookAuthor.getName(), book.getTitle()))
            .reviewId(newReview.getId())
            .receiver(bookAuthor.getName())
            .build();

        jmsTemplate.convertAndSend("mail.review", objectMapper.writeValueAsString(messageDto));
    }
}
