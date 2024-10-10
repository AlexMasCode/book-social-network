package com.ukma.main.service.comments;


import com.ukma.main.service.book.Book;
import com.ukma.main.service.book.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentService {

    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
    }


    public Comment getOne(Long id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("no comment with such id exists"));
    }

    public List<Comment> getAll(CommentDto commentDto) {
        Specification<Comment> filter = createFilter(commentDto);

        return commentRepository.findAll(filter);
    }

    public Comment addComment(CommentDto commentDto) {
        Book book = bookRepository.findById(commentDto.bookId()).orElseThrow(() -> new NoSuchElementException("no book with such id exists"));
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setContent(commentDto.content());
        comment.setUserId(commentDto.userId());
        return commentRepository.save(comment);
    }

    public void updateOne(CommentDto newComment, Long id) {
        Book book = bookRepository.findById(newComment.bookId()).orElseThrow(() -> new NoSuchElementException("no book with such id exists"));
        Comment commentToUpdate = commentRepository.findById(id).orElseThrow();
        commentToUpdate.setContent(newComment.content());
        commentToUpdate.setUserId(newComment.userId());
        commentToUpdate.setBook(book);

        commentRepository.save(commentToUpdate);
    }

    @JmsListener(destination = "comment.complaint", selector = "status = 'APPROVED'")
    public void deleteOne(Long id) {
        commentRepository.deleteById(id);
    }


    private Specification<Comment> createFilter(CommentDto commentDto) {
        if (commentDto == null) {
            return Specification.anyOf();
        }
        List<Specification<Comment>> specs = new ArrayList<>();

        if (commentDto.id() != null) {
            specs.add(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), commentDto.id())
            );
        }

//        if (commentDto.bookId() != null) {
//            specs.add(
//                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bookId"), commentDto.bookId())
//            );
//        }

        return Specification.allOf(specs);
    }

    @JmsListener(destination = "user.deletion", containerFactory = "pubSubFactory")
    public void deleteAllByUserId(String userId){
        System.out.println("Comment deleted");
        commentRepository.deleteAllByUserId(userId);
    }

}
