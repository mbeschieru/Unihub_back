package org.example.unihub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendPasswordResetEmail(String to, String resetLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("resetLink", resetLink);

        String htmlContent = templateEngine.process("password-reset-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendVerificationEmail(String to, String verificationLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("verificationLink", verificationLink);

        String htmlContent = templateEngine.process("verification-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("Verify Your Email");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
} 