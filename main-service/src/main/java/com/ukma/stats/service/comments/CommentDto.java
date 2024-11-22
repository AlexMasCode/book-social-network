package com.ukma.stats.service.comments;

public record CommentDto(
        Long id,
        String content,
        String userId,
        Long bookId
) {
}
