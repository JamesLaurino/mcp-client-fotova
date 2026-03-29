package com.fotova.mcpclientfotova.controller;

import com.fotova.mcpclientfotova.entity.Conversation;
import com.fotova.mcpclientfotova.entity.Message;
import com.fotova.mcpclientfotova.entity.User;
import com.fotova.mcpclientfotova.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {
    
    @Autowired
    private ChatService chatService;

    /**
     * Main chat page - shows all conversations
     */
    @GetMapping("")
    public String showChatPage(HttpSession session, Model model) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Get user's conversations
        List<Conversation> conversations = chatService.getUserConversations(user);

        // Add data to model
        model.addAttribute("user", user);
        model.addAttribute("conversations", conversations);
        model.addAttribute("conversationsCount", conversations.size());

        return "chat";
    }

    /**
     * Specific conversation page
     */
    @GetMapping("/conversation/{conversationId}")
    public String showConversation(@PathVariable Long conversationId,
                                  HttpSession session,
                                  Model model) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Get the conversation (verify it belongs to user)
            Conversation conversation = chatService.getConversationForUser(conversationId, user);

            // Get messages for this conversation
            List<Message> messages = chatService.getConversationMessages(conversationId);

            // Get all user conversations for sidebar
            List<Conversation> conversations = chatService.getUserConversations(user);

            // Add data to model
            model.addAttribute("user", user);
            model.addAttribute("currentConversation", conversation);
            model.addAttribute("messages", messages);
            model.addAttribute("conversations", conversations);

            return "chat-conversation";

        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            return "redirect:/chat";
        }
    }

    /**
     * Create a new conversation
     */
    @PostMapping("/new")
    public String createNewConversation(@RequestParam(required = false) String title,
                                       @RequestParam(required = false) String initialMessage,
                                       HttpSession session) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        // Default title if not provided
        if (title == null || title.trim().isEmpty()) {
            title = "Nouvelle conversation";
        }

        Conversation conversation;

        if (initialMessage != null && !initialMessage.trim().isEmpty()) {
            // Create conversation with initial message
            conversation = chatService.createConversationWithMessage(user, title, initialMessage);
        } else {
            // Create empty conversation
            conversation = chatService.createConversation(user, title);
        }

        // Redirect to the new conversation
        return "redirect:/chat/conversation/" + conversation.getId();
    }

    /**
     * Add a message to a conversation
     */
    @PostMapping("/conversation/{conversationId}/message")
    public String addMessage(@PathVariable Long conversationId,
                            @RequestParam String content,
                            HttpSession session) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Get the conversation (verify it belongs to user)
            Conversation conversation = chatService.getConversationForUser(conversationId, user);

            // Add user message
            chatService.addUserMessage(conversation, content);

            // For now, just add a dummy assistant response
            // Later this will be integrated with MCP server
            chatService.addAssistantMessage(conversation,
                "Message reçu: \"" + content + "\". L'intégration MCP sera implémentée prochainement.");

            // Redirect back to the conversation
            return "redirect:/chat/conversation/" + conversationId;

        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            return "redirect:/chat";
        }
    }

    /**
     * Delete a conversation
     */
    @PostMapping("/conversation/{conversationId}/delete")
    public String deleteConversation(@PathVariable Long conversationId,
                                    HttpSession session) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Verify conversation belongs to user before deleting
            chatService.getConversationForUser(conversationId, user);
            chatService.deleteConversation(conversationId);
        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            // Just continue to main chat page
        }

        return "redirect:/chat";
    }

    /**
     * Update conversation title
     */
    @PostMapping("/conversation/{conversationId}/title")
    public String updateConversationTitle(@PathVariable Long conversationId,
                                         @RequestParam String title,
                                         HttpSession session) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Verify conversation belongs to user before updating
            chatService.getConversationForUser(conversationId, user);

            if (title != null && !title.trim().isEmpty()) {
                chatService.updateConversationTitle(conversationId, title.trim());
            }

            // Redirect back to the conversation
            return "redirect:/chat/conversation/" + conversationId;

        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            return "redirect:/chat";
        }
    }
}