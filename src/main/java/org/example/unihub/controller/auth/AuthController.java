package org.example.unihub.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.AuthRequest;
import org.example.unihub.dto.AuthResponse;
import org.example.unihub.dto.RegisterRequest;
import org.example.unihub.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email or phone number already exists")
    })
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "User registration data", required = true)
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account not verified")
    })
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "User credentials", required = true)
            @Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @Parameter(description = "Refresh token", required = true)
            @RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the current user's tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> logout(
            @Parameter(description = "Access token to invalidate", required = true)
            @RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verifies user's email using the verification token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
        @ApiResponse(responseCode = "409", description = "Email already verified")
    })
    public ResponseEntity<Void> verifyEmail(
            @Parameter(description = "Verification token", required = true)
            @RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email", description = "Sends a new verification email to the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email"),
        @ApiResponse(responseCode = "409", description = "Email already verified")
    })
    public ResponseEntity<Void> resendVerification(
            @Parameter(description = "User's email address", required = true)
            @RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }
} 