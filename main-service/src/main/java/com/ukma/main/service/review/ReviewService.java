package com.ukma.main.service.review;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReviewService {

    ReviewRepository reviewRepository;
    JmsTemplate jmsTemplate;

    public void save(Review review) {
        reviewRepository.save(review);
    }
}
