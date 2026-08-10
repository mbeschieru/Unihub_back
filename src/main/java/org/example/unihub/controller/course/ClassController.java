package org.example.unihub.controller.course;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.ClassDTO;
import org.example.unihub.dto.CreateClassRequest;
import org.example.unihub.dto.UpdateClassRequest;
import org.example.unihub.security.SecurityUtils;
import org.example.unihub.service.ClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ClassDTO> createClass(@RequestBody CreateClassRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        ClassDTO createdClass = classService.createClass(userId, request);
        return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ClassDTO> getClassById(@PathVariable Long classId) {
        ClassDTO classDTO = classService.getClassById(classId);
        return ResponseEntity.ok(classDTO);
    }

    @GetMapping("/my-classes")
    public ResponseEntity<List<ClassDTO>> getMyClasses() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ClassDTO> classes = classService.getMyClasses(userId);
        return ResponseEntity.ok(classes);
    }

    @PostMapping("/join/{inviteUrl}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ClassDTO> joinClass(@PathVariable String inviteUrl) {
        Long userId = SecurityUtils.getCurrentUserId();
        ClassDTO classDTO = classService.joinClass(inviteUrl, userId);
        return ResponseEntity.ok(classDTO);
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<List<String>> getClassStudents(@PathVariable Long classId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<String> studentNames = classService.getClassStudentNames(classId, userId);
        return ResponseEntity.ok(studentNames);
    }

    @PutMapping("/{classId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ClassDTO> updateClass(
            @PathVariable Long classId,
            @RequestBody UpdateClassRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        ClassDTO updatedClass = classService.updateClass(classId, userId, request);
        return ResponseEntity.ok(updatedClass);
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
        Long userId = SecurityUtils.getCurrentUserId();
        classService.deleteClass(classId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{classId}/generate-invite-link")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> generateInviteLink(@PathVariable Long classId) {
        Long userId = SecurityUtils.getCurrentUserId();
        String inviteLink = classService.generateInviteLink(classId, userId);
        return ResponseEntity.ok(inviteLink);
    }

    @GetMapping("/{classId}/invite-link")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> getInviteLink(@PathVariable Long classId) {
        Long userId = SecurityUtils.getCurrentUserId();
        String inviteLink = classService.getInviteLink(classId, userId);
        return ResponseEntity.ok(inviteLink);
    }
} 