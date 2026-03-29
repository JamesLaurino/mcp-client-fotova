package com.fotova.mcpclientfotova.config;

import com.fotova.mcpclientfotova.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        String requestURI = request.getRequestURI();
        
        // Allow access to login page and static resources without authentication
        if (requestURI.startsWith("/login") || 
            requestURI.startsWith("/css/") || 
            requestURI.startsWith("/js/") || 
            requestURI.startsWith("/images/") ||
            requestURI.equals("/") ||
            requestURI.equals("/favicon.ico")) {
            return true;
        }
        
        HttpSession session = request.getSession(false);
        
        // Check if user is authenticated
        if (session == null || session.getAttribute("user") == null) {
            // User not authenticated, redirect to login page
            response.sendRedirect("/login");
            return false;
        }
        
        // User is authenticated, check if session contains a valid User object
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUsername() == null) {
            // Invalid user in session, redirect to login
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }
        
        return true;
    }
}