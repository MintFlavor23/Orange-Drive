package com.safedrive.util;

import com.safedrive.entity.User;
import com.safedrive.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for security-related operations and user validation
 */
@Component
public class SecurityUtil {

    /**
     * Get the current authenticated user from the security context
     * 
     * @return the current user
     * @throws UserNotFoundException if no user is authenticated
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            throw new UserNotFoundException("Invalid user principal");
        }

        return (User) principal;
    }

    /**
     * Get the current user ID
     * 
     * @return the current user ID
     * @throws UserNotFoundException if no user is authenticated
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Validate that the given user ID matches the current authenticated user
     * 
     * @param userId the user ID to validate
     * @throws SecurityException if the user ID doesn't match the current user
     */
    public static void validateUserAccess(Long userId) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new SecurityException("Access denied: User ID mismatch");
        }
    }

    /**
     * Check if the current user is an admin
     * 
     * @return true if the current user is an admin
     */
    public static boolean isCurrentUserAdmin() {
        try {
            User currentUser = getCurrentUser();
            return "ADMIN".equals(currentUser.getRole().name());
        } catch (Exception e) {
            return false;
        }
    }
}