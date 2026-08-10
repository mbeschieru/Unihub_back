package org.example.unihub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.unihub.dto.OpenAiResponseDto;
import org.example.unihub.entity.OpenAiCall;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.errors.RateLimitException;
import org.example.unihub.repository.OpenAiCallRepository;
import org.example.unihub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class OpenAiService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);
    private static final int MAX_CONTEXT_MESSAGES = 10;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OpenAiCallRepository openAiCallRepository;
    private final String apiKey;
    private final String model;
    private final int maxCallsPerMinute;
    private final int maxTokensPerCall;

    private final UserRepository userRepository;

    public OpenAiService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model,
            @Value("${openai.rate-limit.calls-per-minute:20}") int maxCallsPerMinute,
            @Value("${openai.rate-limit.max-tokens:16000}") int maxTokensPerCall,
            OpenAiCallRepository openAiCallRepository,
            RestTemplate restTemplate,
            UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
        this.openAiCallRepository = openAiCallRepository;
        this.apiKey = apiKey;
        this.model = model;
        this.maxCallsPerMinute = maxCallsPerMinute;
        this.maxTokensPerCall = maxTokensPerCall;
        this.userRepository = userRepository;
        
        // Log configuration
        logger.info("OpenAI Service initialized with model: {}, maxCallsPerMinute: {}, maxTokensPerCall: {}", 
            model, maxCallsPerMinute, maxTokensPerCall);
    }

    public OpenAiResponseDto generateCompletion(User user, String prompt, Long taskId) {
        // Check rate limit
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long callsInLastMinute = openAiCallRepository.countCallsByUserAndTimeAfter(user, oneMinuteAgo);
        
        if (callsInLastMinute >= maxCallsPerMinute) {
            logger.warn("Rate limit exceeded for user: {}", user.getId());
            throw RateLimitException.exceededWithRetryAfter(60);
        }

        try {
            // Get task history if taskId is provided
            List<Map<String, Object>> messages = new ArrayList<>();
            if (taskId != null) {
                List<OpenAiCall> taskHistory = openAiCallRepository
                    .findByUserAndTaskIdOrderByTimestampAsc(user, taskId);
                
                // Add previous messages to context (limited to last MAX_CONTEXT_MESSAGES)
                taskHistory.stream()
                    .skip(Math.max(0, taskHistory.size() - MAX_CONTEXT_MESSAGES))
                    .forEach(call -> {
                        Map<String, Object> userMessage = new HashMap<>();
                        userMessage.put("role", "user");
                        userMessage.put("content", call.getPrompt());
                        messages.add(userMessage);

                        Map<String, Object> assistantMessage = new HashMap<>();
                        assistantMessage.put("role", "assistant");
                        assistantMessage.put("content", call.getResponse());
                        messages.add(assistantMessage);
                    });
            }

            // Add current message
            Map<String, Object> currentMessage = new HashMap<>();
            currentMessage.put("role", "user");
            currentMessage.put("content", prompt);
            messages.add(currentMessage);

            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokensPerCall);
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Log request
            logger.debug("Sending request to OpenAI API: {}", requestBody);

            // Make API call
            ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                request,
                Map.class
            );

            // Log response status
            logger.debug("Received response from OpenAI API - Status: {}", response.getStatusCode());

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Failed to get response from OpenAI API - Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to get response from OpenAI API");
            }

            Map<String, Object> responseBody = response.getBody();
            logger.debug("OpenAI API Response Body: {}", responseBody);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                logger.error("No choices in OpenAI response: {}", responseBody);
                throw new RuntimeException("No choices in OpenAI response");
            }

            Map<String, Object> choice = choices.get(0);
            Map<String, Object> messageResponse = (Map<String, Object>) choice.get("message");
            String content = (String) messageResponse.get("content");
            Map<String, Object> usage = (Map<String, Object>) responseBody.get("usage");
            Integer tokensUsed = (Integer) usage.get("total_tokens");

            // Save the call
            OpenAiCall openAiCall = new OpenAiCall();
            openAiCall.setUser(user);
            openAiCall.setPrompt(prompt);
            openAiCall.setResponse(content);
            openAiCall.setTokensUsed(tokensUsed);
            openAiCall.setTaskId(taskId);

            OpenAiCall savedCall = openAiCallRepository.save(openAiCall);
            return convertToDto(savedCall);
        } catch (Exception e) {
            logger.error("Error calling OpenAI API", e);
            if (e.getMessage().contains("429")) {
                throw RateLimitException.exceededWithRetryAfter(60);
            }
            throw new RuntimeException("Error calling OpenAI API: " + e.getMessage(), e);
        }
    }

    public List<OpenAiResponseDto> getAllHistoryByTask (Long userId , Long taskId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User does not exist"));

        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")))
            throw new BusinessException("User is not a professor");

        return openAiCallRepository.findAll()
                .stream()
                .filter(submission -> submission.getTaskId().equals(taskId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List <OpenAiResponseDto> getAllHistoryByTaskAndStudent(Long userId, Long studentId , Long taskId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User does not exist"));

        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")))
            throw new BusinessException("User is not a professor");


        return openAiCallRepository.findAll()
                .stream()
                .filter(submission -> submission.getTaskId().equals(taskId))
                .filter(submission -> submission.getUser().getId().equals(studentId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public List<OpenAiResponseDto> getUserHistory(User user) {
        return openAiCallRepository.findByUserOrderByTimestampDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<OpenAiResponseDto> getTaskHistory(User user, Long taskId) {
        return openAiCallRepository.findByUserAndTaskIdOrderByTimestampAsc(user, taskId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private OpenAiResponseDto convertToDto(OpenAiCall call) {
        return OpenAiResponseDto.builder()
                .id(call.getId())
                .prompt(call.getPrompt())
                .response(call.getResponse())
                .timestamp(call.getTimestamp())
                .tokensUsed(call.getTokensUsed())
                .userId(call.getUser().getId())
                .taskId(call.getTaskId())
                .build();
    }
} 