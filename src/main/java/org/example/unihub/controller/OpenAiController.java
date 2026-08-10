package org.example.unihub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.unihub.dto.OpenAiResponseDto;
import org.example.unihub.entity.User;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/openai")
@Tag(name = "OpenAI", description = "OpenAI API integration endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OpenAiController {

    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/completion/{taskId}")
    @Operation(
        summary = "Generate AI completion for a task",
        description = "Generates a completion using OpenAI's API with task context and rate limiting"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Completion generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid prompt"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<OpenAiResponseDto> generateCompletion(
            @Parameter(description = "The task ID to generate completion for")
            @PathVariable Long taskId,
            @Parameter(description = "The prompt to generate a completion for")
            @RequestBody String prompt) {
        try {
            User user = SecurityUtils.getCurrentUser();
            OpenAiResponseDto response = openAiService.generateCompletion(user, prompt, taskId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(429).build(); // Too Many Requests
        }
    }

    @GetMapping("/history")
    @Operation(
        summary = "Get user's AI call history",
        description = "Retrieves the history of OpenAI API calls made by the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<OpenAiResponseDto>> getUserHistory() {
        User user = SecurityUtils.getCurrentUser();
        List<OpenAiResponseDto> history = openAiService.getUserHistory(user);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/task/{taskId}/history")
    @Operation(
        summary = "Get task's AI call history",
        description = "Retrieves the history of OpenAI API calls for a specific task"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<List<OpenAiResponseDto>> getTaskHistory(
            @Parameter(description = "The task ID")
            @PathVariable Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<OpenAiResponseDto> history = openAiService.getAllHistoryByTask(userId, taskId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/student/{studentId}/task/{taskId}/history")
    public ResponseEntity<List<OpenAiResponseDto>> getTaskHistoryByUser(
            @PathVariable Long taskId,
            @PathVariable Long studentId
    )
    {
        Long userId = SecurityUtils.getCurrentUserId();
        List<OpenAiResponseDto> history = openAiService.getAllHistoryByTaskAndStudent(userId,studentId,taskId);
        return ResponseEntity.ok(history);
    }

}