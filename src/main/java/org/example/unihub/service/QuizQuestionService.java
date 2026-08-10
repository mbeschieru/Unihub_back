package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.QuizQuestionDTO;
import org.example.unihub.dto.QuizQuestionRequest;
import org.example.unihub.entity.Quiz;
import org.example.unihub.entity.QuizQuestion;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.QuizQuestionRepository;
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
public class QuizQuestionService {
    private final QuizQuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public QuizQuestionDTO createQuestion(Long quizId, QuizQuestionRequest questionRequest, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException("Quiz not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a professor and has access to this quiz
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !quiz.getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to create questions for this quiz");
        }

        QuizQuestion question = mapper.toQuizQuestion(questionRequest);
        question.setQuiz(quiz);

        QuizQuestion savedQuestion = questionRepository.save(question);
        return mapper.toQuizQuestionDTO(savedQuestion);
    }

    @Transactional(readOnly = true)
    public List<QuizQuestionDTO> getQuestionsByQuizId(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException("Quiz not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !quiz.getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this quiz's questions");
        }

        return questionRepository.findByQuizId(quizId)
                .stream()
                .map(mapper::toQuizQuestionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizQuestionDTO getQuestionById(Long id, Long userId) {
        QuizQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !question.getQuiz().getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this question");
        }

        return mapper.toQuizQuestionDTO(question);
    }

    @Transactional
    public QuizQuestionDTO updateQuestion(Long id, QuizQuestionRequest questionRequest, Long userId) {
        QuizQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this quiz
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !question.getQuiz().getLaboratory().getClassEntity().getProfessor().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to update this question");
        }

        mapper.toQuizQuestion(questionRequest, question);
        QuizQuestion savedQuestion = questionRepository.save(question);
        return mapper.toQuizQuestionDTO(savedQuestion);
    }

    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        QuizQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this quiz
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !question.getQuiz().getLaboratory().getClassEntity().getProfessor().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this question");
        }

        questionRepository.deleteById(id);
    }
}