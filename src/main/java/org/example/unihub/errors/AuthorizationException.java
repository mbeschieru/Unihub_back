package org.example.unihub.errors;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BaseException {
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN, "AUTHORIZATION_ERROR");
    }

    public static AuthorizationException notAuthorized() {
        return new AuthorizationException("Not authorized (Admin only)");
    }

    public static AuthorizationException notStudent() {
        return new AuthorizationException("User is not a student");
    }

    public static AuthorizationException notAuthorizedToView() {
        return new AuthorizationException("User is not authorized to view this content");
    }

    public static AuthorizationException notAuthorizedToViewClassStudents() {
        return new AuthorizationException("User is not authorized to view class students");
    }
} 