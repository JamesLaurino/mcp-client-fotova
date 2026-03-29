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
     * GET /chat - Main chat page with conversations list
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
        model.addAttribute("currentConversation", null);
        model.addAttribute("messages", List.of());

        return "chat";
    }

    /**
     * GET /chat/{conversationId} - Specific conversation page
     */
    @GetMapping("/{conversationId}")
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
            model.addAttribute("conversationsCount", conversations.size());

            return "chat";

        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            return "redirect:/chat";
        }
    }

    /**
     * POST /chat - Create a new conversation (handles "New Chat" button)
     */
    @PostMapping("")
    public String createNewConversation(HttpSession session) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

            // Create empty conversation
        Conversation conversation = chatService.createConversation(user, "Nouvelle conversation");
        // Redirect to the new conversation
        return "redirect:/chat/" + conversation.getId();
    }

    /**
     * POST /chat/send - Send a message (redirects to conversation page)
     * This handles messages sent from the main chat form
     */
    @PostMapping("/send")
    public String sendMessage(@RequestParam Long conversationId,
                            @RequestParam String message,
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
            chatService.addUserMessage(conversation, message);

            // For Milestone 4: just add a dummy assistant response
            // (LLM integration will come later)
            chatService.addAssistantMessage(conversation,
                "Message reçu : \"" + message + "\"\n\n" +
                "Réponse du système (Mode démo) : Ceci est une réponse automatique. " +
                "L'intégration du LLM via MCP sera implémentée dans les prochaines milestones.");
            // Redirect back to the conversation (page reload)
            return "redirect:/chat/" + conversationId;

        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            return "redirect:/chat";
        }
    }

    /**
     * Alternative route: POST /chat/{conversationId}/send
     * Handles messages sent from specific conversation page
     */
    @PostMapping("/{conversationId}/send")
    public String addMessage(@PathVariable Long conversationId,
                            @RequestParam String message,
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
            chatService.addUserMessage(conversation, message);

            // For Milestone 4: just add a dummy assistant response
            chatService.addAssistantMessage(conversation,
                "Message reçu : \"" + message + "\"\n\n" +
                "Réponse du système (Mode démo) : Ceci est une réponse automatique. " +
                "L'intégration du LLM via MCP sera implémentée dans les prochaines milestones.");
            // Redirect back to the conversation (page reload)
            return "redirect:/chat/" + conversationId;

        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            return "redirect:/chat";
        }
    }

    /**
     * POST /chat/conversation/{conversationId}/delete - Delete a conversation
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
     * POST /chat/conversation/{conversationId}/title - Update conversation title
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
            return "redirect:/chat/" + conversationId;

        } catch (IllegalArgumentException e) {
            // Conversation not found or doesn't belong to user
            return "redirect:/chat";
        }
    }
}
