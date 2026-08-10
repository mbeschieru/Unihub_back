package org.example.unihub.errors;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_ERROR");
    }

    public static AuthenticationException notAuthenticated() {
        return new AuthenticationException("User not authenticated");
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid credentials");
    }

    public static AuthenticationException invalidRefreshToken() {
        return new AuthenticationException("Invalid refresh token");
    }

    public static AuthenticationException emailNotVerified() {
        return new AuthenticationException("Please verify your email before logging in");
    }
} 