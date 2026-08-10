package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.QuizDTO;
import org.example.unihub.dto.QuizRequest;
import org.example.unihub.entity.Laboratory;
import org.example.unihub.entity.Quiz;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.LaboratoryRepository;
import org.example.unihub.repository.QuizRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public QuizDTO createQuiz(Long laboratoryId, QuizRequest quizRequest, Long userId) {
        Laboratory laboratory = laboratoryRepository.findById(laboratoryId)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a professor and has access to this laboratory
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !laboratory.getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to create quizzes for this laboratory");
        }

        Quiz quiz = mapper.toQuiz(quizRequest);
        quiz.setLaboratory(laboratory);

        Quiz savedQuiz = quizRepository.save(quiz);
        return mapper.toQuizDTO(savedQuiz);
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesByLaboratoryId(Long laboratoryId, Long userId) {
        Laboratory laboratory = laboratoryRepository.findById(laboratoryId)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !laboratory.getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this laboratory's quizzes");
        }

        return quizRepository.findByLaboratoryId(laboratoryId)
                .stream()
                .map(mapper::toQuizDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id, Long userId) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Quiz not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !quiz.getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this quiz");
        }

        return mapper.toQuizDTO(quiz);
    }

    @Transactional
    public QuizDTO updateQuiz(Long id, QuizRequest quizRequest, Long userId) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Quiz not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this quiz
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !quiz.getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to update this quiz");
        }

        mapper.toQuiz(quizRequest, quiz);
        Quiz savedQuiz = quizRepository.save(quiz);
        return mapper.toQuizDTO(savedQuiz);
    }

    @Transactional
    public void deleteQuiz(Long id, Long userId) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Quiz not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this quiz
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !quiz.getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this quiz");
        }

        quizRepository.delete(quiz);
    }
} 