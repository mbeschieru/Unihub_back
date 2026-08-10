package org.example.unihub.controller.assessment.finaltest;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.FinalTestDTO;
import org.example.unihub.dto.FinalTestRequest;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.FinalTestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/final-tests")
@RequiredArgsConstructor
public class FinalTestController {
    private final FinalTestService testService;

    @PostMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<FinalTestDTO> createTest(
            @PathVariable Long laboratoryId,
            @RequestBody FinalTestRequest testRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        FinalTestDTO createdTest = testService.createTest(laboratoryId, testRequest, userId);
        return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<FinalTestDTO>> getTestsByLaboratoryId(@PathVariable Long laboratoryId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<FinalTestDTO> tests = testService.getTestsByLaboratoryId(laboratoryId, userId);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/{testId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<FinalTestDTO> getTestById(@PathVariable Long testId) {
        Long userId = SecurityUtils.getCurrentUserId();
        FinalTestDTO test = testService.getTestById(testId, userId);
        return ResponseEntity.ok(test);
    }

    @PutMapping("/{testId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<FinalTestDTO> updateTest(
            @PathVariable Long testId,
            @RequestBody FinalTestRequest testRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        FinalTestDTO updatedTest = testService.updateTest(testId, testRequest, userId);
        return ResponseEntity.ok(updatedTest);
    }

    @DeleteMapping("/{testId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteTest(@PathVariable Long testId) {
        Long userId = SecurityUtils.getCurrentUserId();
        testService.deleteTest(testId, userId);
        return ResponseEntity.noContent().build();
    }
} 