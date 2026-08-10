package org.example.unihub.controller.assessment.quiz;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.QuizQuestionDTO;
import org.example.unihub.dto.QuizQuestionRequest;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.QuizQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuizQuestionController {
    private final QuizQuestionService questionService;

    @PostMapping("/quiz/{quizId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<QuizQuestionDTO> createQuestion(
            @PathVariable Long quizId,
            @RequestBody QuizQuestionRequest questionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizQuestionDTO createdQuestion = questionService.createQuestion(quizId, questionRequest, userId);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsByQuizId(@PathVariable Long quizId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<QuizQuestionDTO> questions = questionService.getQuestionsByQuizId(quizId, userId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<QuizQuestionDTO> getQuestionById(@PathVariable Long questionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizQuestionDTO question = questionService.getQuestionById(questionId, userId);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<QuizQuestionDTO> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuizQuestionRequest questionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizQuestionDTO updatedQuestion = questionService.updateQuestion(questionId, questionRequest, userId);
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