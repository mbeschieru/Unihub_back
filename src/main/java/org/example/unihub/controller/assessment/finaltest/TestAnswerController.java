package org.example.unihub.controller.assessment.finaltest;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TestAnswerDTO;
import org.example.unihub.dto.TestAnswerRequest;
import org.example.unihub.dto.TestAnswerResponseDTO;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.TestAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/final-tests/answers")
@RequiredArgsConstructor
public class TestAnswerController {
    private final TestAnswerService answerService;

    @PostMapping("/{questionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<TestAnswerResponseDTO> submitAnswer(
            @PathVariable Long questionId,
            @RequestBody TestAnswerRequest answerRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        TestAnswerResponseDTO submittedAnswer = answerService.submitAnswer(questionId, userId, answerRequest);
        return ResponseEntity.ok(submittedAnswer);
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<TestAnswerResponseDTO>> getAnswersByTestId(@PathVariable Long testId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TestAnswerResponseDTO> answers = answerService.getAnswersByTestId(testId, userId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<TestAnswerResponseDTO>> getAnswersByQuestionId(@PathVariable Long questionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TestAnswerResponseDTO> answers = answerService.getAnswersByQuestionId(questionId, userId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/my-answers")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<TestAnswerResponseDTO>> getMyAnswers() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TestAnswerResponseDTO> answers = answerService.getAnswersByStudentId(userId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/{answerId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<TestAnswerResponseDTO> getAnswerById(@PathVariable Long answerId) {
        Long userId = SecurityUtils.getCurrentUserId();
        TestAnswerResponseDTO answer = answerService.getAnswerById(answerId, userId);
        return ResponseEntity.ok(answer);
    }
} 