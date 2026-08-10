package org.example.unihub.controller.course;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.LaboratoryRequest;
import org.example.unihub.dto.LaboratoryDTO;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.LaboratoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/laboratories")
@RequiredArgsConstructor
public class LaboratoryController {
    private final LaboratoryService laboratoryService;

    @PostMapping("/class/{classId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<LaboratoryDTO> createLaboratory(
            @PathVariable Long classId,
            @RequestBody LaboratoryRequest createLaboratoryRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        LaboratoryDTO createdLaboratory = laboratoryService.createLaboratory(classId, userId, createLaboratoryRequest);
        return new ResponseEntity<>(createdLaboratory, HttpStatus.CREATED);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<LaboratoryDTO>> getLaboratoriesByClassId(@PathVariable Long classId) {
        List<LaboratoryDTO> laboratories = laboratoryService.getLaboratoriesByClassId(classId);
        return ResponseEntity.ok(laboratories);
    }

    @GetMapping("/{laboratoryId}")
    public ResponseEntity<LaboratoryDTO> getLaboratoryById(@PathVariable Long laboratoryId) {
        LaboratoryDTO laboratory = laboratoryService.getLaboratoryById(laboratoryId);
        return ResponseEntity.ok(laboratory);
    }

    @PutMapping("/{laboratoryId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<LaboratoryDTO> updateLaboratory(
            @PathVariable Long laboratoryId,
            @RequestBody LaboratoryRequest LaboratoryRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        LaboratoryDTO updatedLaboratory = laboratoryService.updateLaboratory(laboratoryId, userId, LaboratoryRequest);
        return ResponseEntity.ok(updatedLaboratory);
    }

    @DeleteMapping("/{laboratoryId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteLaboratory(@PathVariable Long laboratoryId) {
        Long userId = SecurityUtils.getCurrentUserId();
        laboratoryService.deleteLaboratory(laboratoryId, userId);
        return ResponseEntity.noContent().build();
    }
} 