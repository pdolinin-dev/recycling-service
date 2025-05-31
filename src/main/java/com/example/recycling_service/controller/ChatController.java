package com.example.recycling_service.controller;

import com.example.recycling_service.dto.ChatDto;
import com.example.recycling_service.model.ChatMessage;
import com.example.recycling_service.repository.ChatMessageRepository;
import com.example.recycling_service.security.JwtTokenProvider;
import com.example.recycling_service.service.ChatService;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.UserRepository;

import jakarta.websocket.Session;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.logging.*;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/api/chat")
@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{chatId}/messages/{advertisementId}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(
            @PathVariable String chatId,
            @PathVariable Long advertisementId,
            Authentication authentication) throws BadRequestException {

        // 1. Проверяем формат chatId
        if (!chatId.matches("\\d+_\\d+")) {
            throw new BadRequestException("Invalid chat ID format");
        }

        // 2. Получаем текущего пользователя
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3. Проверяем, что пользователь участник чата
        String[] userIds = chatId.split("_");
        if (!userIds[0].equals(currentUser.getId().toString()) &&
                !userIds[1].equals(currentUser.getId().toString())) {
            throw new AccessDeniedException("You don't have access to this chat");
        }

        // 4. Получаем сообщения
        List<ChatMessage> messages = chatMessageRepository.findByChatIdAndAdvertisementIdAfterOrderByTimestamp(chatId, advertisementId);
        log.info(chatId.toString());
        log.info(advertisementId.toString());
        log.info(messages.toString());
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/chat.send/{chatId}")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessage sendMessage(
            @Payload ChatMessage chatMessage,
            @DestinationVariable String chatId,
            SimpMessageHeaderAccessor headerAccessor) {

        // Проверяем, что сообщение содержит необходимые данные
        if (chatMessage.getSender() == null) {
            throw new SecurityException("Sender ID is missing in the message");
        }

        // Дополнительная проверка аутентификации через заголовки
        String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid authorization token" + authHeader);
        }

        // Извлекаем токен и проверяем его
        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtTokenProvider.getUsernameFromToken(token);
        } catch (Exception e) {
            throw new SecurityException("Invalid token");
        }

        // Проверяем соответствие senderId из сообщения и токена
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getId().equals(chatMessage.getSender())) {
            throw new SecurityException("Sender ID doesn't match authenticated user");
        }

        // Устанавливаем недостающие поля
        chatMessage.setReceiver(chatMessage.getReceiver());
        chatMessage.setChatId(chatId);
        chatMessage.setTimestamp(LocalDateTime.now());
        log.info(chatMessage.toString());
        chatMessage.setAdvertisementId(chatMessage.getAdvertisementId());

        return chatMessageRepository.save(chatMessage);
    }
//@MessageMapping("/chat.send/{chatId}")
//@SendTo("/topic/chat/{chatId}")
//public ChatMessage sendMessage(
//        @Payload Map<String, Object> payload,
//        @DestinationVariable String chatId,
//        SimpMessageHeaderAccessor headerAccessor) throws BadRequestException {
//
//    // Проверка аутентификации
//    String token = headerAccessor.getFirstNativeHeader("Authorization");
//    if (token == null || !token.startsWith("Bearer ")) {
//        throw new SecurityException("Missing token");
//    }
//
//    String username = jwtTokenProvider.getUsernameFromToken(token.substring(7));
//    User sender = userRepository.findByUsername(username)
//            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//    // Разбираем chatId
//    String[] userIds = chatId.split("_");
//    if (userIds.length != 2) {
//        throw new BadRequestException("Invalid chat ID format");
//    }
//
//    Long userId1 = Long.parseLong(userIds[0]);
//    Long userId2 = Long.parseLong(userIds[1]);
//
//    // Проверяем, что отправитель - участник чата
//    if (!sender.getId().equals(userId1) && !sender.getId().equals(userId2)) {
//        throw new AccessDeniedException("You can't send messages to this chat");
//    }
//
//    // Создаем и сохраняем сообщение
//    ChatMessage message = new ChatMessage();
//    message.setSender(sender.getId());
//    message.setReceiver(sender.getId().equals(userId1) ? userId2 : userId1);
//    message.setContent((String) payload.get("content"));
//    message.setTimestamp(LocalDateTime.now());
//
//    return chatMessageRepository.save(message);
//}

    // REST endpoint for chat initialization
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<String> initializeChat(@PathVariable String chatId) {
        return ResponseEntity.ok("Chat " + chatId + " initialized");
    }

    // Получение списка чатов пользователя
    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats(Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Получаем все уникальные listing_id (чаты), где участвует пользователь
        List<String> chatIds = chatMessageRepository.findDistinctChatIdsByUserId(currentUser.getId());

        List<ChatDto> chats = chatIds.stream()
                .map(chatId -> {
                    // Разбираем chatId на участников
                    String[] userIds = chatId.split("_");
                    Long otherUserId = userIds[0].equals(currentUser.getId().toString()) ?
                            Long.parseLong(userIds[1]) : Long.parseLong(userIds[0]);

                    User interlocutor = userRepository.findById(otherUserId)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                    // Получаем последнее сообщение в чате (может быть null)
                    ChatMessage lastMessage = chatMessageRepository.findTopByChatIdOrderByTimestampDesc(chatId);

                    return new ChatDto(
                            chatId,
                            interlocutor.getId(),
                            interlocutor.getUsername(),
                            // interlocutor.getAvatarUrl(), // раскомментируйте, если есть поле avatarUrl
                            lastMessage != null ? lastMessage.getContent() : null,
                            lastMessage != null ? lastMessage.getTimestamp() : null,
                            lastMessage != null ? lastMessage.getAdvertisementId() : null
                    );
                })
                .sorted((c1, c2) -> {
                    // Сортируем по времени последнего сообщения (новые сверху)
                    if (c1.getLastMessageTime() == null) return 1;
                    if (c2.getLastMessageTime() == null) return -1;
                    return c2.getLastMessageTime().compareTo(c1.getLastMessageTime());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(chats);
    }

    // Генерация ID чата (user1_user2, где user1 < user2)
    private String generateChatId(Long userId1, Long userId2) {
        return userId1 < userId2 ?
                userId1 + "_" + userId2 :
                userId2 + "_" + userId1;
    }
}
