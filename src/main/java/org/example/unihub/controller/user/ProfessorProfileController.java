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
import org.example.unihub.dto.ProfessorProfileDTO;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.ProfessorProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professors/profile")
@RequiredArgsConstructor
public class ProfessorProfileController {

    private final ProfessorProfileService professorProfileService;

    @PostMapping
    @Operation(summary = "Create professor profile", description = "Creates a new profile for the authenticated professor user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profile created successfully",
            content = @Content(schema = @Schema(implementation = ProfessorProfileDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a professor"),
        @ApiResponse(responseCode = "409", description = "Profile already exists")
    })
    public ResponseEntity<ProfessorProfileDTO> createProfessorProfile(
            @Parameter(description = "Professor profile data", required = true)
            @Valid @RequestBody ProfessorProfileDTO professorProfileDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        ProfessorProfileDTO createdProfile = professorProfileService.createProfessorProfile(userId, professorProfileDTO);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get own professor profile", description = "Retrieves the profile of the authenticated professor user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProfessorProfileDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a professor"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfessorProfileDTO> getProfessorProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        ProfessorProfileDTO professorProfile = professorProfileService.getProfessorProfileByUserId(userId);
        return ResponseEntity.ok(professorProfile);
    }

    @PutMapping
    @Operation(summary = "Update professor profile", description = "Updates the profile of the authenticated professor user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = ProfessorProfileDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a professor"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfessorProfileDTO> updateProfessorProfile(
            @Parameter(description = "Updated professor profile data", required = true)
            @Valid @RequestBody ProfessorProfileDTO professorProfileDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        ProfessorProfileDTO updatedProfile = professorProfileService.updateProfessorProfile(userId, professorProfileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping
    @Operation(summary = "Delete professor profile", description = "Deletes the profile of the authenticated professor user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a professor"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<Void> deleteProfessorProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        professorProfileService.deleteProfessorProfile(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get own professor profile (alternative endpoint)", 
              description = "Alternative endpoint to retrieve the profile of the authenticated professor user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProfessorProfileDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "User is not a professor"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfessorProfileDTO> getMyProfessorProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        ProfessorProfileDTO professorProfileDTO = professorProfileService.getProfessorProfileByUserId(userId);
        return ResponseEntity.ok(professorProfileDTO);
    }
} 