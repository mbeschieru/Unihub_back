package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.config.FrontendProperties;
import org.example.unihub.entity.User;
import org.example.unihub.entity.VerificationToken;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final FrontendProperties frontendProperties;
    private static final int TOKEN_EXPIRY_HOURS = 24;

    @Transactional
    public void sendVerificationEmail(User user) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUserEmail(user.getEmail());

        // Create new token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                .used(false)
                .build();
        tokenRepository.save(verificationToken);

        // Send email
        String verificationLink = frontendProperties.getVerifyEmailUrl() + "?token=" + token;
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
        } catch (Exception e) {
            throw new BusinessException("Failed to send verification email");
        }
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            throw new BusinessException("Verification token has already been used");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
        
        if (user.isEmailVerified()) {
            throw new BusinessException("Email is already verified");
        }

        sendVerificationEmail(user);
    }
} 