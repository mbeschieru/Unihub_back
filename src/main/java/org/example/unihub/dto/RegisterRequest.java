package org.example.unihub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Schema(description = "User's email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User's password (min 8 chars, at least one uppercase, one lowercase, one digit, one special character)", example = "Password123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", 
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
    private String password;

    @Schema(description = "User's first name", example = "John")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;


    @Schema(description = "User's last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    @Schema(description = "User's phone number", example = "+1234567890")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\s-()]{10,}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;


} 