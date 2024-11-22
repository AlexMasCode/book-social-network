package com.ukma.stats.service.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ukma.stats.service.review.dto.CreateReviewDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReviewController {

    ReviewService reviewService;

    @PostMapping
    public void save(@RequestBody @Valid CreateReviewDto createReviewDto) throws JsonProcessingException {
        reviewService.save(createReviewDto, (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
    }
}
