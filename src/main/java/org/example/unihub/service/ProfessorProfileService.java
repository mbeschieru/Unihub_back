package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.ProfessorProfileDTO;
import org.example.unihub.entity.ProfessorProfile;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.ProfessorProfileRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProfessorProfileService {

    private final ProfessorProfileRepository professorProfileRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final UserService userService;

    @Transactional
    public ProfessorProfileDTO createProfessorProfile(Long userId, ProfessorProfileDTO professorProfileDTO) {
        validateProfessorProfileDTO(professorProfileDTO);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found with id: " + userId));

        if (user.isProfileActive()) {
            throw new BusinessException(" User has already a profile " + userId);
        }

        ProfessorProfile professorProfile = mapper.toProfessorProfile(professorProfileDTO);

        user.setProfileActive(true);
        user.setProfileType("Professor");

        User updatedUser = userRepository.save(user);
        userService.clearCache();

        professorProfile.setUser(updatedUser);
        
        ProfessorProfile savedProfile = professorProfileRepository.save(professorProfile);
        return mapper.toProfessorProfileDTO(savedProfile);
    }

    @Transactional(readOnly = true)
    public ProfessorProfileDTO getProfessorProfileByUserId(Long userId) {
        return professorProfileRepository.findByUserId(userId)
                .map(mapper::toProfessorProfileDTO)
                .orElseThrow(() -> new BusinessException("Professor profile not found for user id: " + userId));
    }

    @Transactional
    public ProfessorProfileDTO updateProfessorProfile(Long userId, ProfessorProfileDTO professorProfileDTO) {
        validateProfessorProfileDTO(professorProfileDTO);
        
        ProfessorProfile existingProfile = professorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Professor profile not found for user id: " + userId));
        
        ProfessorProfile updatedProfile = mapper.toProfessorProfile(professorProfileDTO);
        updatedProfile.setId(existingProfile.getId());
        updatedProfile.setUser(existingProfile.getUser());
        
        ProfessorProfile savedProfile = professorProfileRepository.save(updatedProfile);
        return mapper.toProfessorProfileDTO(savedProfile);
    }

    @Transactional
    public void deleteProfessorProfile(Long userId) {
        ProfessorProfile professorProfile = professorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Professor profile not found for user id: " + userId));
        
        professorProfileRepository.delete(professorProfile);
    }

    private void validateProfessorProfileDTO(ProfessorProfileDTO professorProfileDTO) {
        if (professorProfileDTO == null) {
            throw new BusinessException("Professor profile data cannot be null");
        }
        
        if (!StringUtils.hasText(professorProfileDTO.getProfessorId())) {
            throw new BusinessException("Professor ID cannot be empty");
        }
        
        if (!StringUtils.hasText(professorProfileDTO.getDepartment())) {
            throw new BusinessException("Department cannot be empty");
        }
    }
} 