package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.unihub.dto.ClassDTO;
import org.example.unihub.dto.CreateClassRequest;
import org.example.unihub.dto.UpdateClassRequest;
import org.example.unihub.entity.ClassEntity;
import org.example.unihub.entity.ProfessorProfile;
import org.example.unihub.entity.StudentProfile;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.ClassRepository;
import org.example.unihub.repository.ProfessorProfileRepository;
import org.example.unihub.repository.StudentProfileRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final ProfessorProfileRepository professorProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final Mapper mapper;

    @Transactional
    public ClassDTO createClass(Long userId, CreateClassRequest request) {
        validateCreateClassRequest(request);
        
        ProfessorProfile professor = professorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("User is not a professor"));

        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(request.getName());
        classEntity.setDescription(request.getDescription());
        classEntity.setYear(request.getYear());
        classEntity.setProfessor(professor);
        
        ClassEntity savedClass = classRepository.save(classEntity);
        return mapper.toClassDTO(savedClass);
    }

    @Transactional(readOnly = true)
    public ClassDTO getClassById(Long classId) {
        return classRepository.findById(classId)
                .map(mapper::toClassDTO)
                .orElseThrow(() -> new BusinessException("Class not found with id: " + classId));
    }

    @Transactional(readOnly = true)
    public List<ClassDTO> getMyClasses(Long userId) {
        // Check if user is a professor
        ProfessorProfile professor = professorProfileRepository.findByUserId(userId).orElse(null);
        if (professor != null) {
            return classRepository.findByProfessorId(professor.getId()).stream()
                    .map(mapper::toClassDTO)
                    .collect(Collectors.toList());
        }

        // Check if user is a student
        StudentProfile student = studentProfileRepository.findByUserId(userId).orElse(null);
        if (student != null) {
            return classRepository.findByStudentsId(student.getId()).stream()
                    .map(mapper::toClassDTO)
                    .collect(Collectors.toList());
        }

        throw new BusinessException("User is neither a professor nor a student");
    }

    @Transactional
    public ClassDTO joinClass(String inviteUrl, Long userId) {
        ClassEntity classEntity = classRepository.findByInviteUrl(inviteUrl)
                .orElseThrow(() -> new BusinessException("Class not found with invite URL: " + inviteUrl));

        StudentProfile student = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("User is not a student"));

        if (classEntity.getStudents().contains(student)) {
            throw new BusinessException("Student is already enrolled in this class");
        }

        classEntity.getStudents().add(student);
        ClassEntity savedClass = classRepository.save(classEntity);
        return mapper.toClassDTO(savedClass);
    }

    @Transactional(readOnly = true)
    public List<String> getClassStudentNames(Long classId, Long userId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException("Class not found with id: " + classId));

        // Check if user is either the professor or a student in the class
        boolean isAuthorized = false;
        
        // Check if user is the professor
        if (classEntity.getProfessor().getUser().getId().equals(userId)) {
            isAuthorized = true;
        }
        
        // Check if user is a student in the class
        if (!isAuthorized) {
            StudentProfile student = studentProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new BusinessException("User is not a student"));
            isAuthorized = classEntity.getStudents().contains(student);
        }

        if (!isAuthorized) {
            throw new BusinessException("User is not authorized to view class students");
        }

        return classEntity.getStudents().stream()
                .map(student -> student.getUser().getFirstName() + " " + student.getUser().getLastName())
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassDTO updateClass(Long classId, Long userId, UpdateClassRequest request) {
        validateUpdateClassRequest(request);
        
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException("Class not found with id: " + classId));

        // Verify that the user is the professor who created the class
        if (!classEntity.getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("Only the professor who created the class can update it");
        }

        // Update class fields
        if (request.getName() != null) {
            classEntity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            classEntity.setDescription(request.getDescription());
        }
        if (request.getYear() != null) {
            classEntity.setYear(request.getYear());
        }

        ClassEntity updatedClass = classRepository.save(classEntity);
        return mapper.toClassDTO(updatedClass);
    }

    @Transactional
    public void deleteClass(Long classId, Long userId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException("Class not found with id: " + classId));

        // Verify that the user is the professor who created the class
        if (!classEntity.getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("Only the professor who created the class can delete it");
        }

        classRepository.delete(classEntity);
    }

    private void validateCreateClassRequest(CreateClassRequest request) {
        if (request == null) {
            throw new BusinessException("Class data cannot be null");
        }
        
        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException("Class name cannot be empty");
        }
        
        if (request.getYear() == null || request.getYear() < 1) {
            throw new BusinessException("Year must be a positive number");
        }
    }

    private void validateUpdateClassRequest(UpdateClassRequest request) {
        if (request == null) {
            throw new BusinessException("Update data cannot be null");
        }
        
        if (request.getName() != null && !StringUtils.hasText(request.getName())) {
            throw new BusinessException("Class name cannot be empty");
        }
        
        if (request.getYear() != null && request.getYear() < 1) {
            throw new BusinessException("Year must be a positive number");
        }
    }

    @Transactional
    public String generateInviteLink(Long classId, Long userId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException("Class not found with id: " + classId));

        // Verify that the user is the professor who created the class
        if (!classEntity.getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("Only the professor who created the class can generate invite links");
        }

        // Generate new invite URL
        String newInviteUrl = RandomStringUtils.randomAlphanumeric(6);
        classEntity.setInviteUrl(newInviteUrl);
        classRepository.save(classEntity);

        return newInviteUrl;
    }

    @Transactional(readOnly = true)
    public String getInviteLink(Long classId, Long userId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException("Class not found with id: " + classId));

        // Verify that the user is the professor who created the class
        if (!classEntity.getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("Only the professor who created the class can view invite links");
        }

        return classEntity.getInviteUrl();
    }
} 