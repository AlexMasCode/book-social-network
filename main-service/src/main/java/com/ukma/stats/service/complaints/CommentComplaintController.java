package com.ukma.stats.service.complaints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment-complaints")
public class CommentComplaintController {

    private final CommentComplaintService commentComplaintService;

    public CommentComplaintController(CommentComplaintService complaintService) {
        this.commentComplaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<CommentComplaint> createCommentComplaint(@RequestBody CommentComplaintDto commentComplaintDto){

        return ResponseEntity.ok(commentComplaintService.createCommentComplaint(commentComplaintDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentComplaint> resolveComplaint(@PathVariable("id") Long id, @RequestBody String status){
        return ResponseEntity.ok(commentComplaintService.resolveCommentComplaint(id, ComplaintStatus.valueOf(status.toUpperCase())));
    }

    @GetMapping
    public ResponseEntity<List<CommentComplaint>> getAll(){
        return ResponseEntity.ok(commentComplaintService.getAll());
    }


}
