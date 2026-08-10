package org.example.unihub.controller.assessment.finaltest;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TestQuestionDTO;
import org.example.unihub.dto.TestQuestionRequest;
import org.example.unihub.dto.TestQuestionResponseDTO;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.TestQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/final-tests/question")
@RequiredArgsConstructor
public class TestQuestionController {
    private final TestQuestionService questionService;

    @PostMapping("/{testId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TestQuestionResponseDTO> createQuestion(
            @PathVariable Long testId,
            @RequestBody TestQuestionRequest questionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        TestQuestionResponseDTO createdQuestion = questionService.createQuestion(testId, questionRequest, userId);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<TestQuestionResponseDTO>> getQuestionsByTestId(@PathVariable Long testId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TestQuestionResponseDTO> questions = questionService.getQuestionsByTestId(testId, userId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<TestQuestionResponseDTO> getQuestionById(@PathVariable Long questionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        TestQuestionResponseDTO question = questionService.getQuestionById(questionId, userId);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<TestQuestionResponseDTO> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody TestQuestionRequest questionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        TestQuestionResponseDTO updatedQuestion = questionService.updateQuestion(questionId, questionRequest, userId);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        questionService.deleteQuestion(questionId, userId);
        return ResponseEntity.noContent().build();
    }
} 