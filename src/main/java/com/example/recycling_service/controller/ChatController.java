package com.example.recycling_service.controller;

import com.example.recycling_service.model.ChatMessage;
import com.example.recycling_service.repository.ChatMessageRepository;
import com.example.recycling_service.service.ChatService;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.UserRepository;

import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;
import java.util.logging.*;

@RequestMapping("/api/chat")
@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatService chatService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable String chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserDetails user) {

        // Проверка прав доступа к чату
        if (!chatService.hasAccessToChat(user.getUsername(), chatId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<ChatMessage> messages = chatMessageRepository.findByChatId(chatId, pageable);

        return ResponseEntity.ok(messages.getContent());
    }

    @MessageMapping("/chat.send/{chatId}")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessage sendMessage(
            @Payload ChatMessage chatMessage,
            @DestinationVariable String chatId,
            Principal principal) {

        // Явная проверка principal
        if (principal == null) {
            throw new SecurityException("User not authenticated");
        }

        // Получаем аутентификацию из principal
        Authentication auth = (Authentication) principal;
        String username = auth.getName();

        // Получаем пользователя из БД
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Устанавливаем поля сообщения
        chatMessage.setSender(user.getId());
        chatMessage.setChatId(chatId);
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
    }

    // REST endpoint for chat initialization
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<String> initializeChat(@PathVariable String chatId) {
        return ResponseEntity.ok("Chat " + chatId + " initialized");
    }

}