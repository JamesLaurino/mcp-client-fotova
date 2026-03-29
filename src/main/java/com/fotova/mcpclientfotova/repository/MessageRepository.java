package com.fotova.mcpclientfotova.repository;

import com.fotova.mcpclientfotova.entity.Conversation;
import com.fotova.mcpclientfotova.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Find all messages for a specific conversation
    List<Message> findByConversation(Conversation conversation);
    
    // Find all messages for a specific conversation ID
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt ASC")
    List<Message> findByConversationId(@Param("conversationId") Long conversationId);
    
    // Find messages with pagination
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt ASC")
    List<Message> findMessagesByConversationId(@Param("conversationId") Long conversationId);
    
    // Count messages in a conversation
    long countByConversation(Conversation conversation);
    
    // Find the last message in a conversation
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC LIMIT 1")
    Message findLastMessageByConversationId(@Param("conversationId") Long conversationId);
}