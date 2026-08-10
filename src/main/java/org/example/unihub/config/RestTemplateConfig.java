package org.example.unihub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Configuration
public class RestTemplateConfig {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String errorBody = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                
                logger.error("API Error Response - Status: {}, Body: {}", response.getStatusCode(), errorBody);
                throw new RuntimeException("API Error: " + errorBody);
            }
        });
        return restTemplate;
    }
} 