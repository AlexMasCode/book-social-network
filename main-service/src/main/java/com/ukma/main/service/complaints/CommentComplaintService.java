package com.ukma.main.service.complaints;

import jakarta.jms.Message;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentComplaintService {

    private final CommentComplaintRepository commentComplaintRepository;
    private final JmsTemplate jmsTemplate;

    public CommentComplaintService(CommentComplaintRepository commentComplaintRepository, JmsTemplate jmsTemplate) {
        this.commentComplaintRepository = commentComplaintRepository;
        this.jmsTemplate = jmsTemplate;
    }

    public CommentComplaint createCommentComplaint(CommentComplaintDto commentComplaintDto){
        var commentComplaint = new CommentComplaint();
        commentComplaint.setCommentId(commentComplaintDto.commentId());
        commentComplaint.setReason(commentComplaintDto.reason());
        return commentComplaintRepository.save(commentComplaint);
    }

    public CommentComplaint resolveCommentComplaint(Long complaintId, ComplaintStatus status){
        var commentComplaint = commentComplaintRepository.findById(complaintId).orElseThrow(()->new NoSuchElementException("Complaint with such id does not exist"));
        commentComplaint.setStatus(status);
        commentComplaintRepository.save(commentComplaint);

        jmsTemplate.send("comment.complaint", session -> {
            Message message = session.createObjectMessage(commentComplaint.commentId);
            message.setStringProperty("status", status.name());
            return message;
        });

        return commentComplaint;
    }

    public List<CommentComplaint> getAll(){
        return commentComplaintRepository.findAll();
    }

}
