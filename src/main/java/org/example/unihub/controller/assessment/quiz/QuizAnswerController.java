package org.example.unihub.controller.assessment.quiz;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.QuizAnswerDTO;
import org.example.unihub.dto.QuizAnswerRequest;
import org.example.unihub.dto.QuizAnswerResponseDTO;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.QuizAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/quizzes/answers")
@RequiredArgsConstructor
public class QuizAnswerController {
    private final QuizAnswerService answerService;

    @PostMapping("/question/{questionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizAnswerResponseDTO> submitAnswer(
            @PathVariable Long questionId,
            @RequestBody QuizAnswerRequest answerRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizAnswerResponseDTO submittedAnswer = answerService.submitAnswer(questionId, userId, answerRequest);
        return ResponseEntity.ok(submittedAnswer);
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<QuizAnswerResponseDTO>> getAnswersByQuestionId(@PathVariable Long questionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<QuizAnswerResponseDTO> answers = answerService.getAnswersByQuestionId(questionId, userId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/my-answers")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<QuizAnswerResponseDTO>> getMyAnswers() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<QuizAnswerResponseDTO> answers = answerService.getAnswersByStudentId(userId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/{answerId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<QuizAnswerResponseDTO> getAnswerById(@PathVariable Long answerId) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuizAnswerResponseDTO answer = answerService.getAnswerById(answerId, userId);
        return ResponseEntity.ok(answer);
    }
} 