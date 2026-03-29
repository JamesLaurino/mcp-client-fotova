package com.fotova.mcpclientfotova.controller;

import com.fotova.mcpclientfotova.entity.User;
import com.fotova.mcpclientfotova.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Identifiants incorrects.");
        }

        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès.");
        }

        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        
        Optional<User> userOptional = authService.authenticate(username, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Store user in session
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            
            // Set session timeout (1 hour)
            session.setMaxInactiveInterval(3600);

            return "redirect:/chat";
        }
        
        // Invalid credentials
        model.addAttribute("error", true);
        model.addAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect.");
        return "login";
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}