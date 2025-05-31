package com.example.recycling_service.controller;

import com.example.recycling_service.dto.EmailRequest;
import com.example.recycling_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public String sendEmail(@RequestBody EmailRequest request, Authentication authentication) {
        String username = authentication.getName(); // предполагается, что это ID пользователя
        request.setFromUserName(username);

        emailService.sendSimpleMessage(
                request.getTo(),
                request.getSubject(),
                request.getText(),
                request.getFromUserName()
        );
        return "Email sent successfully!";
    }

    @PostMapping("/send-html-email")
    public String sendHtmlEmail(@RequestBody EmailRequest request, Authentication authentication) {
        String username = authentication.getName();
        request.setFromUserName(username);

        try {
            String htmlContent = "<h1>" + request.getSubject() + "</h1>"
                    + "<p>" + request.getText() + "</p>"
                    + "<hr><p><strong>User ID:</strong> " + request.getFromUserName() + "</p>";

            emailService.sendHtmlMessage(
                    request.getTo(),
                    request.getSubject(),
                    htmlContent,
                    request.getFromUserName()
            );

            return "HTML email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }


}