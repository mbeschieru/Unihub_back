package org.example.unihub.security;

import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtils {
    
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            throw new BusinessException("Invalid user type");
        }
        
        User user = (User) principal;
        if (!user.isEmailVerified()) {
            throw new BusinessException("Please verify your email before accessing this resource");
        }
        
        return user;
    }
    
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
} 