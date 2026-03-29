package com.fotova.mcpclientfotova.service;

import com.fotova.mcpclientfotova.entity.Conversation;
import com.fotova.mcpclientfotova.entity.Message;
import com.fotova.mcpclientfotova.entity.User;
import com.fotova.mcpclientfotova.repository.ConversationRepository;
import com.fotova.mcpclientfotova.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    /**
     * Create a new conversation for a user
     */
    @Transactional
    public Conversation createConversation(User user, String title) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setTitle(title);
        conversation.setCreatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }
    
    /**
     * Create a conversation with initial message
     */
    @Transactional
    public Conversation createConversationWithMessage(User user, String title, String initialMessage) {
        Conversation conversation = createConversation(user, title);
        
        // Add initial message
        Message message = new Message();
        message.setConversation(conversation);
        message.setRole(Message.MessageRole.USER);
        message.setContent(initialMessage);
        message.setCreatedAt(LocalDateTime.now());
        
        messageRepository.save(message);
        
        return conversation;
    }
    
    /**
     * Add a message to a conversation
     */
    @Transactional
    public Message addMessage(Conversation conversation, Message.MessageRole role, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        
        // Update conversation title if it's the first message
        if (messageRepository.countByConversation(conversation) == 1) {
            String truncatedContent = content.length() > 50 ? content.substring(0, 47) + "..." : content;
            conversation.setTitle(truncatedContent);
            conversationRepository.save(conversation);
        }
        
        return savedMessage;
    }
    
    /**
     * Add a user message to a conversation
     */
    @Transactional
    public Message addUserMessage(Conversation conversation, String content) {
        return addMessage(conversation, Message.MessageRole.USER, content);
    }
    
    /**
     * Add an assistant message to a conversation
     */
    @Transactional
    public Message addAssistantMessage(Conversation conversation, String content) {
        return addMessage(conversation, Message.MessageRole.ASSISTANT, content);
    }
    
    /**
     * Get all conversations for a user
     */
    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUser(user);
    }
    
    /**
     * Get a conversation with its messages
     */
    public Conversation getConversationWithMessages(Long conversationId) {
        return conversationRepository.findByIdWithMessages(conversationId);
    }
    
    /**
     * Get messages for a conversation
     */
    public List<Message> getConversationMessages(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }
    
    /**
     * Get a conversation by ID
     */
    public Conversation getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));
    }
    
    /**
     * Get a conversation by ID and verify it belongs to the user
     */
    public Conversation getConversationForUser(Long conversationId, User user) {
        Conversation conversation = getConversation(conversationId);
        
        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Conversation does not belong to user");
        }
        
        return conversation;
    }
    
    /**
     * Delete a conversation and all its messages
     */
    @Transactional
    public void deleteConversation(Long conversationId) {
        conversationRepository.deleteById(conversationId);
    }
    
    /**
     * Update conversation title
     */
    @Transactional
    public Conversation updateConversationTitle(Long conversationId, String newTitle) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));
        
        conversation.setTitle(newTitle);
        return conversationRepository.save(conversation);
    }
    
    /**
     * Get recent conversations for a user (with pagination)
     */
    public List<Conversation> getRecentConversations(User user, int limit) {
        List<Conversation> allConversations = conversationRepository.findByUser(user);
        return allConversations.stream()
                .limit(limit)
                .toList();
    }
    
    /**
     * Count conversations for a user
     */
    public long countUserConversations(User user) {
        return conversationRepository.countByUser(user);
    }
}