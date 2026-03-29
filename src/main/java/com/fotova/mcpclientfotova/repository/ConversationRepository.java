package com.fotova.mcpclientfotova.repository;

import com.fotova.mcpclientfotova.entity.Conversation;
import com.fotova.mcpclientfotova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    // Find all conversations for a specific user
    List<Conversation> findByUser(User user);
    
    // Find all conversations for a specific user ID
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Conversation> findByUserId(@Param("userId") Long userId);
    
    // Find conversation by ID with eager loading of messages
    @Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.messages WHERE c.id = :conversationId")
    Conversation findByIdWithMessages(@Param("conversationId") Long conversationId);
    
    // Count conversations for a user
    long countByUser(User user);
}