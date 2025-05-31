package com.example.recycling_service.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text, String fromUserName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("paveldolinin03@gmail.com"); // технический адрес
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text + "\n\nUsername: " + fromUserName);
        emailSender.send(message);
    }

    public void sendHtmlMessage(String to, String subject, String htmlContent, String fromUserId)
            throws jakarta.mail.MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("paveldolinin03@gmail.com"); // технический адрес
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }

}
