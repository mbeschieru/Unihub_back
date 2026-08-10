package org.example.unihub.controller.assessment.quiz;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.QuizDTO;
import org.example.unihub.dto.QuizRequest;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @PostMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<QuizDTO> createQuiz(
            @PathVariable Long laboratoryId,
            @RequestBody QuizRequest quizRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizDTO createdQuiz = quizService.createQuiz(laboratoryId, quizRequest, userId);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<QuizDTO>> getQuizzesByLaboratoryId(@PathVariable Long laboratoryId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<QuizDTO> quizzes = quizService.getQuizzesByLaboratoryId(laboratoryId, userId);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long quizId) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizDTO quiz = quizService.getQuizById(quizId, userId);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizRequest quizRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizDTO updatedQuiz = quizService.updateQuiz(quizId, quizRequest, userId);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        Long userId = SecurityUtils.getCurrentUserId();
        quizService.deleteQuiz(quizId, userId);
        return ResponseEntity.noContent().build();
    }
} 