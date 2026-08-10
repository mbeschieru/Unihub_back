package org.example.unihub.service;

import lombok.RequiredArgsConstructor;

import org.example.unihub.config.FrontendProperties;
import org.example.unihub.entity.PasswordResetToken;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.PasswordResetTokenRepository;
import org.example.unihub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final FrontendProperties frontendProperties;
    private static final int TOKEN_EXPIRY_HOURS = 24;

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Delete any existing tokens for this user
        tokenRepository.deleteByUserEmail(email);

        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                .used(false)
                .build();
        tokenRepository.save(resetToken);

        // Send email
        String resetLink = frontendProperties.getResetPasswordUrl() + "?token=" + token;
        try {
            emailService.sendPasswordResetEmail(email, resetLink);
        } catch (Exception e) {
            throw new BusinessException("Failed to send password reset email");
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new BusinessException("Reset token has already been used");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
} 