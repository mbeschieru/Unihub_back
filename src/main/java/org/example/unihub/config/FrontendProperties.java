package org.example.unihub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "frontend")
public class FrontendProperties {
    private String baseUrl;
    private String resetPasswordPath = "/reset-password";
    private String verifyEmailPath = "/verify-email";
    private String oauth2RedirectPath = "/oauth2/redirect";

    public String getResetPasswordUrl() {
        return baseUrl + resetPasswordPath;
    }

    public String getVerifyEmailUrl() {
        return baseUrl + verifyEmailPath;
    }

    public String getOauth2RedirectUrl() {
        return baseUrl + oauth2RedirectPath;
    }
} 