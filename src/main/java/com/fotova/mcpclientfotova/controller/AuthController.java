package com.fotova.mcpclientfotova.controller;

import com.fotova.mcpclientfotova.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
    
    // Simple in-memory user storage (will be replaced with database later)
    private static final Map<String, User> users = new HashMap<>();
    
    static {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRole("ADMIN");
        users.put("admin", admin);
        
        // Create regular user
        User user = new User();
        user.setUsername("user");
        user.setPassword("user");
        user.setRole("USER");
        users.put("user", user);
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        
        User user = users.get(username);
        
        if (user != null && user.getPassword().equals(password)) {
            // Store user in session
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            
            return "redirect:/chat";
        }
        
        // Invalid credentials
        return "redirect:/login?error=true";
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}