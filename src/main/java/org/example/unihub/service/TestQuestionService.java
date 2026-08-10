package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TestQuestionDTO;
import org.example.unihub.dto.TestQuestionRequest;
import org.example.unihub.dto.TestQuestionResponseDTO;
import org.example.unihub.entity.FinalTest;
import org.example.unihub.entity.TestQuestion;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.FinalTestRepository;
import org.example.unihub.repository.TestQuestionRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestQuestionService {
    private final TestQuestionRepository questionRepository;
    private final FinalTestRepository testRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public TestQuestionResponseDTO createQuestion(Long testId, TestQuestionRequest questionRequest, Long userId) {
        FinalTest test = testRepository.findById(testId)
                .orElseThrow(() -> new BusinessException("Test not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a professor and has access to this test
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !test.getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to create questions for this test");
        }

        TestQuestion question = mapper.toTestQuestion(questionRequest);
        question.setFinalTest(test);

        TestQuestion savedQuestion = questionRepository.save(question);
        return mapper.toTestQuestionResponseDTO(savedQuestion);
    }

    @Transactional(readOnly = true)
    public List<TestQuestionResponseDTO> getQuestionsByTestId(Long testId, Long userId) {
        FinalTest test = testRepository.findById(testId)
                .orElseThrow(() -> new BusinessException("Test not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !test.getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to these questions");
        }

        return questionRepository.findByFinalTestId(testId)
                .stream()
                .map(mapper::toTestQuestionResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestQuestionResponseDTO getQuestionById(Long id, Long userId) {
        TestQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !question.getFinalTest().getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this question");
        }

        return mapper.toTestQuestionResponseDTO(question);
    }

    @Transactional
    public TestQuestionResponseDTO updateQuestion(Long id, TestQuestionRequest questionRequest, Long userId) {
        TestQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this test
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !question.getFinalTest().getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to update this question");
        }

        mapper.toTestQuestion(questionRequest, question);
        TestQuestion savedQuestion = questionRepository.save(question);
        return mapper.toTestQuestionResponseDTO(savedQuestion);
    }

    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        TestQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this test
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !question.getFinalTest().getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this question");
        }

        questionRepository.deleteById(id);
    }
} 