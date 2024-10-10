package com.ukma.main.service.comments;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentDto comment){
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(comment));
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAll(@RequestBody(required = false) CommentDto commentDto) {
        return ResponseEntity.ok(commentService.getAll(commentDto));
    }

    @GetMapping("/{id}")
    public Comment getOne(@PathVariable("id") Long id) {
        return commentService.getOne(id);
    }

    @PatchMapping("/{id}")
    public void updateOne(@RequestBody CommentDto commentDto, @PathVariable Long id) {
        commentService.updateOne(commentDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable("id") Long id) {
        commentService.deleteOne(id);
    }
}