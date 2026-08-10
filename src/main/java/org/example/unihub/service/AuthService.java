package org.example.unihub.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.AuthRequest;
import org.example.unihub.dto.AuthResponse;
import org.example.unihub.dto.RegisterRequest;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.security.JwtUtils;
import org.example.unihub.utils.Mapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final Mapper mapper;
    private final VerificationService verificationService;
    private final Set<String> blacklistedTokens = new HashSet<>();

    @Transactional
    public AuthResponse register(@Valid RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("Phone number already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .emailVerified(false)
                .profileActive(false)
                .build();

        userRepository.save(user);
        verificationService.sendVerificationEmail(user);


        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationTime())
                .build();
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        
        if (!user.isEmailVerified()) {
            throw new BusinessException("Please verify your email before logging in");
        }

        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationTime())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtUtils.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!jwtUtils.validateToken(refreshToken, user)) {
            throw new BusinessException("Invalid refresh token");
        }

        String newAccessToken = jwtUtils.generateToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationTime())
                .build();
    }

    @Transactional
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            blacklistedTokens.add(token);
        }
        SecurityContextHolder.clearContext();
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public void verifyEmail(String token) {
        verificationService.verifyEmail(token);
    }

    public void resendVerificationEmail(String email) {
        verificationService.resendVerificationEmail(email);
    }

    public AuthResponse handleOAuth2Success() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationTime())
                .build();
    }
} 