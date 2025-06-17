package com.example.recycling_service.dto;

import com.example.recycling_service.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDto {
    private String chatId;
    private Long interlocutorId;
    private String interlocutorName;
    private String interFullName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long advertisementId;

    public ChatDto(String chatId, Long interlocutorId, String interlocutorName, String interFullName, String lastMessage, LocalDateTime lastMessageTime, Long advertisementId) {
        this.chatId = chatId;
        this.interlocutorId = interlocutorId;
        this.interlocutorName = interlocutorName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.advertisementId = advertisementId;
        this.interFullName = interFullName;
    }
}
