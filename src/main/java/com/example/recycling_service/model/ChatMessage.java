/*
    Класс представления сообщений
 */

package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="message_id")
    private Long id;

    @Column(name="listing_id", nullable = false)
    private String chatId;

    @Column(name="sender_id", nullable = false)
    private Long sender;
    private String content;

    @CreationTimestamp
    private LocalDateTime timestamp;
}