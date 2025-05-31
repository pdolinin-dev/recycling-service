package com.example.recycling_service.repository;

import com.example.recycling_service.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    Page<ChatMessage> findByChatId(String chatId, Pageable pageable);
    // Находим последнее сообщение в чате
    ChatMessage findTopByChatIdOrderByTimestampDesc(String chatId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ChatMessage m SET m.sender = :deletedUserId WHERE m.sender = :originalUserId")
    void updateUserSenderReferencesToDeleted(@Param("originalUserId") Long originalUserId, @Param("deletedUserId") Long deletedUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ChatMessage m SET m.receiver = :deletedUserId WHERE m.receiver = :originalUserId")
    void updateUserReceiverReferencesToDeleted(@Param("originalUserId") Long originalUserId, @Param("deletedUserId") Long deletedUserId);

    @Query("SELECT DISTINCT m FROM ChatMessage m WHERE m.chatId = :chatId AND m.advertisementId = :advertisementId")
    List<ChatMessage> findByChatIdAndAdvertisementIdAfterOrderByTimestamp(String chatId, Long advertisementId);

    @Query("SELECT DISTINCT m.chatId FROM ChatMessage m WHERE m.sender = :userId OR m.receiver = :userId")
    List<String> findDistinctChatIdsByUserId(@Param("userId") Long userId);
    // Находим всех уникальных собеседников для пользователя
    @Query("SELECT DISTINCT CASE WHEN m.sender = :userId THEN m.receiver ELSE m.sender END " +
            "FROM ChatMessage m WHERE m.sender = :userId OR m.receiver = :userId")
    List<Long> findDistinctInterlocutors(@Param("userId") Long userId);

    // Находим последнее сообщение между двумя пользователями
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender = :userId1 AND m.receiver = :userId2) OR " +
            "(m.sender = :userId2 AND m.receiver = :userId1) " +
            "ORDER BY m.timestamp DESC LIMIT 1")
    Optional<ChatMessage> findLastMessageBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );

    List<ChatMessage> AdvertisementId(Long advertisementId);
}