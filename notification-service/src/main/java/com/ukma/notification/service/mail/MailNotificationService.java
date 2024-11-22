package com.ukma.notification.service.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.notification.service.mail.dto.ReviewMailMessageDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MailNotificationService {

    final JavaMailSender mailSender;
    final ObjectMapper objectMapper;

    @Value("${spring.mail.username}")
    String senderEmail;

    @JmsListener(destination = "mail.review")
    public void sendEmailToUser(String reviewMailMessageDtoAsString) throws JsonProcessingException {
        ReviewMailMessageDto reviewMailMessageDto = objectMapper.readValue(reviewMailMessageDtoAsString, ReviewMailMessageDto.class);
        sendEmail(senderEmail, reviewMailMessageDto.getReceiver(), reviewMailMessageDto.getTitle(), reviewMailMessageDto.getContent());
    }

    public void sendEmail(String from, String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
