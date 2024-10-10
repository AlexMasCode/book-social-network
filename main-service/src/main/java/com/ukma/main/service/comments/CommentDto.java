package com.ukma.main.service.comments;

public record CommentDto(
        Long id,
        String content,
        String userId,
        Long bookId
) {
}
