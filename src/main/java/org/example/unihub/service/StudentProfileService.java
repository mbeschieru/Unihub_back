package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.StudentProfileDTO;
import org.example.unihub.entity.StudentProfile;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.StudentProfileRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final UserService userService;

    @Transactional
    public StudentProfileDTO createStudentProfile(Long userId, StudentProfileDTO studentProfileDTO) {
        validateStudentProfileDTO(studentProfileDTO);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found with id: " + userId));

        if (user.isProfileActive()) {
            throw new BusinessException(" User has already a profile " + userId);
        }

        StudentProfile studentProfile = mapper.toStudentProfile(studentProfileDTO);

        user.setProfileActive(true);
        user.setProfileType("Student");

        User updatedUser = userRepository.save(user);
        userService.clearCache();

        studentProfile.setUser(updatedUser);
        
        StudentProfile savedProfile = studentProfileRepository.save(studentProfile);
        return mapper.toStudentProfileDTO(savedProfile);
    }

    @Transactional(readOnly = true)
    public StudentProfileDTO getStudentProfileByUserId(Long userId) {
        return studentProfileRepository.findByUserId(userId)
                .map(mapper::toStudentProfileDTO)
                .orElseThrow(() -> new BusinessException("Student profile not found for user id: " + userId));
    }

    @Transactional
    public StudentProfileDTO updateStudentProfile(Long userId, StudentProfileDTO studentProfileDTO) {
        validateStudentProfileDTO(studentProfileDTO);
        
        StudentProfile existingProfile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found for user id: " + userId));
        
        StudentProfile updatedProfile = mapper.toStudentProfile(studentProfileDTO);
        updatedProfile.setId(existingProfile.getId());
        updatedProfile.setUser(existingProfile.getUser());
        
        StudentProfile savedProfile = studentProfileRepository.save(updatedProfile);
        return mapper.toStudentProfileDTO(savedProfile);
    }

    @Transactional
    public void deleteStudentProfile(Long userId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found for user id: " + userId));
        
        studentProfileRepository.delete(studentProfile);
    }

    private void validateStudentProfileDTO(StudentProfileDTO studentProfileDTO) {
        if (studentProfileDTO == null) {
            throw new BusinessException("Student profile data cannot be null");
        }
        
        if (!StringUtils.hasText(studentProfileDTO.getStudentId())) {
            throw new BusinessException("Student ID cannot be empty");
        }
        
        if (studentProfileDTO.getYearOfStudy() == null || studentProfileDTO.getYearOfStudy() < 1) {
            throw new BusinessException("Year of study must be a positive number");
        }
    }
} 