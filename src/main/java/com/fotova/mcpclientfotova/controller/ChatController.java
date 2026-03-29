package com.fotova.mcpclientfotova.controller;

import com.fotova.mcpclientfotova.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {
    
    @GetMapping("/chat")
    public String showChatPage(HttpSession session, Model model) {
        // Check if user is authenticated
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Add user info to model for the template
        model.addAttribute("user", user);
        
        return "chat";
    }
}