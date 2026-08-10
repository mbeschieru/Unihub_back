package org.example.unihub.controller.assessment.task;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TaskGradingRequest;
import org.example.unihub.dto.TaskSubmissionRequest;
import org.example.unihub.dto.TaskSubmissionResponseDTO;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.TaskSubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/tasks/{taskId}/submissions")
@RequiredArgsConstructor
@Tag(name = "Task Submissions", description = "APIs for managing task submissions and grading")
public class TaskSubmissionController {
    private final TaskSubmissionService submissionService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit task", description = "Submits a task solution. Only students can submit tasks.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task submitted successfully"),
        @ApiResponse(responseCode = "403", description = "Only students can submit tasks"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskSubmissionResponseDTO> submitTask(
            @Parameter(description = "ID of the task") @PathVariable Long taskId,
            @Parameter(description = "Submission details") @RequestBody TaskSubmissionRequest submissionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskSubmissionResponseDTO submittedTask = submissionService.submitTask(taskId, userId, submissionRequest);
        return ResponseEntity.ok(submittedTask);
    }

    @PutMapping("/{submissionId}/grade")
    @PreAuthorize("hasRole('PROFESSOR')")
    @Operation(summary = "Grade submission", description = "Grades a task submission. Only professors can grade submissions.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Submission graded successfully"),
        @ApiResponse(responseCode = "403", description = "Only professors can grade submissions"),
        @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    public ResponseEntity<TaskSubmissionResponseDTO> gradeSubmission(
            @Parameter(description = "ID of the task") @PathVariable Long taskId,
            @Parameter(description = "ID of the submission") @PathVariable Long submissionId,
            @Parameter(description = "Grading details") @RequestBody TaskGradingRequest gradingRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskSubmissionResponseDTO gradedSubmission = submissionService.gradeSubmission(
            submissionId, 
            gradingRequest.getGrade(), 
            gradingRequest.getFeedback(), 
            userId
        );
        return ResponseEntity.ok(gradedSubmission);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    @Operation(summary = "Get all submissions", description = "Retrieves all submissions for a task. Accessible by both students and professors.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of submissions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<List<TaskSubmissionResponseDTO>> getSubmissionsByTaskId(
            @Parameter(description = "ID of the task") @PathVariable Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TaskSubmissionResponseDTO> submissions = submissionService.getSubmissionsByTaskId(taskId, userId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get my submissions", description = "Retrieves all submissions made by the current student.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of submissions retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Only students can view their submissions")
    })
    public ResponseEntity<List<TaskSubmissionResponseDTO>> getMySubmissions(
            @Parameter(description = "ID of the task") @PathVariable Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<TaskSubmissionResponseDTO> submissions = submissionService.getMySubmissionsByTaskId(taskId,userId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{submissionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    @Operation(summary = "Get submission by ID", description = "Retrieves a specific submission by its ID. Accessible by both students and professors.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Submission retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    public ResponseEntity<TaskSubmissionResponseDTO> getSubmissionById(
            @Parameter(description = "ID of the task") @PathVariable Long taskId,
            @Parameter(description = "ID of the submission") @PathVariable Long submissionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskSubmissionResponseDTO submission = submissionService.getSubmissionById(submissionId, userId);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
    public ResponseEntity<List<String>> getStudentsNameBySubmission( @Parameter  Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<String > students = submissionService.getStudentsByTaskId(taskId, userId);
        return ResponseEntity.ok(students);
    }

} 