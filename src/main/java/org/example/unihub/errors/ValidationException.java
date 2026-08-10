package org.example.unihub.errors;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
    }

    public static ValidationException invalidInput(String field, String message) {
        return new ValidationException(String.format("Invalid %s: %s", field, message));
    }

    public static ValidationException invalidToken() {
        return new ValidationException("Invalid verification token");
    }

    public static ValidationException tokenUsed() {
        return new ValidationException("Verification token has already been used");
    }

    public static ValidationException tokenExpired() {
        return new ValidationException("Verification token has expired");
    }

    public static ValidationException emailExists() {
        return new ValidationException("Email already exists");
    }

    public static ValidationException phoneExists() {
        return new ValidationException("Phone number already exists");
    }

    public static ValidationException profileExists() {
        return new ValidationException("User has already a profile");
    }
} 