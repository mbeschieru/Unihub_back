package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.FinalTestDTO;
import org.example.unihub.dto.FinalTestRequest;
import org.example.unihub.entity.FinalTest;
import org.example.unihub.entity.Laboratory;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.FinalTestRepository;
import org.example.unihub.repository.LaboratoryRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinalTestService {
    private final FinalTestRepository testRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public FinalTestDTO createTest(Long laboratoryId, FinalTestRequest testRequest, Long userId) {
        Laboratory laboratory = laboratoryRepository.findById(laboratoryId)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a professor and has access to this laboratory
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !laboratory.getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to create tests for this laboratory");
        }

        FinalTest test = mapper.toFinalTest(testRequest);
        test.setLaboratory(laboratory);

        FinalTest savedTest = testRepository.save(test);
        return mapper.toFinalTestDTO(savedTest);
    }

    @Transactional(readOnly = true)
    public List<FinalTestDTO> getTestsByLaboratoryId(Long laboratoryId, Long userId) {
        Laboratory laboratory = laboratoryRepository.findById(laboratoryId)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !laboratory.getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to these tests");
        }

        return testRepository.findByLaboratoryId(laboratoryId)
                .stream()
                .map(mapper::toFinalTestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FinalTestDTO getTestById(Long testId, Long userId) {
        FinalTest test = testRepository.findById(testId)
                .orElseThrow(() -> new BusinessException("Test not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !test.getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this test");
        }

        return mapper.toFinalTestDTO(test);
    }

    @Transactional
    public FinalTestDTO updateTest(Long testId, FinalTestRequest testRequest, Long userId) {
        FinalTest test = testRepository.findById(testId)
                .orElseThrow(() -> new BusinessException("Test not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this test
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !test.getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to update this test");
        }

        mapper.toFinalTest(testRequest, test);
        FinalTest updatedTest = testRepository.save(test);
        return mapper.toFinalTestDTO(updatedTest);
    }

    @Transactional
    public void deleteTest(Long testId, Long userId) {
        FinalTest test = testRepository.findById(testId)
                .orElseThrow(() -> new BusinessException("Test not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this test
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !test.getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this test");
        }

        testRepository.deleteById(testId);
    }
} 