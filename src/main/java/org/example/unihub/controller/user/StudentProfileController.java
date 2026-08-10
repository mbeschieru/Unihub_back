package org.example.unihub.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.StudentProfileDTO;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.StudentProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping
    @Operation(summary = "Create student profile", description = "Creates a new profile for the authenticated student user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profile created successfully",
            content = @Content(schema = @Schema(implementation = StudentProfileDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a student"),
        @ApiResponse(responseCode = "409", description = "Profile already exists")
    })
    public ResponseEntity<StudentProfileDTO> createStudentProfile(
            @Parameter(description = "Student profile data", required = true)
            @Valid @RequestBody StudentProfileDTO studentProfileDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        StudentProfileDTO createdProfile = studentProfileService.createStudentProfile(userId, studentProfileDTO);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get own student profile", description = "Retrieves the profile of the authenticated student user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = StudentProfileDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a student"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<StudentProfileDTO> getStudentProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        StudentProfileDTO studentProfile = studentProfileService.getStudentProfileByUserId(userId);
        return ResponseEntity.ok(studentProfile);
    }

    @PutMapping
    @Operation(summary = "Update student profile", description = "Updates the profile of the authenticated student user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = StudentProfileDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a student"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<StudentProfileDTO> updateStudentProfile(
            @Parameter(description = "Updated student profile data", required = true)
            @Valid @RequestBody StudentProfileDTO studentProfileDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        StudentProfileDTO updatedProfile = studentProfileService.updateStudentProfile(userId, studentProfileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping
    @Operation(summary = "Delete student profile", description = "Deletes the profile of the authenticated student user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a student"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<Void> deleteStudentProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        studentProfileService.deleteStudentProfile(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get own student profile (alternative endpoint)", 
              description = "Alternative endpoint to retrieve the profile of the authenticated student user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = StudentProfileDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a student"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<StudentProfileDTO> getMyStudentProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        StudentProfileDTO studentProfileDTO = studentProfileService.getStudentProfileByUserId(userId);
        return ResponseEntity.ok(studentProfileDTO);
    }
} 