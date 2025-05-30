package com.example.recycling_service.dto;

import com.example.recycling_service.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatDto {
    private String chatId;
    private Long interlocutorId;
    private String interlocutorName;
    //private String interlocutorAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long advertisementId;

    public ChatDto(String chatId, Long interlocutorId, String interlocutorName, String interlocutorAvatar, String lastMessage, LocalDateTime lastMessageTime, Long advertisementId) {
        this.chatId = chatId;
        this.interlocutorId = interlocutorId;
        this.interlocutorName = interlocutorName;
        //this.interlocutorAvatar = interlocutorAvatar;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.advertisementId = advertisementId;
    }
}
