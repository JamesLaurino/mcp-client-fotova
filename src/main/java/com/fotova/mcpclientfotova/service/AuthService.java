package com.fotova.mcpclientfotova.service;

import com.fotova.mcpclientfotova.entity.User;
import com.fotova.mcpclientfotova.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @PostConstruct
    public void init() {
        initializeDefaultUsers();
    }
    
    private void initializeDefaultUsers() {
        // Create admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin"); // In production, this should be hashed
            admin.setRole("ADMIN");
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("Admin user created");
        }
        
        // Create regular user if it doesn't exist
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword("user"); // In production, this should be hashed
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            System.out.println("Regular user created");
        }
    }
    
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Simple password check (in production, use password hashing)
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    public boolean isUserAuthenticated(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}