/*
    Класс представления сообщений
 */

package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
// TODO: Enable when chat module is rewritten.
// @Entity
@Table(name = "messages")
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="message_id")
    private Long id;

    @Column(name="listing_id", nullable = false)
    private String chatId;

    @Getter
    @Setter
    @Column(name="sender_id", nullable = false)
    private Long sender;

    @Column(name="receiver_id", nullable = false)
    private Long receiver;
    private String content;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column(name="advertisement_id", nullable = false)
    private Long advertisementId;
}
