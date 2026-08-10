package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.QuizAnswerDTO;
import org.example.unihub.dto.QuizAnswerRequest;
import org.example.unihub.dto.QuizAnswerResponseDTO;
import org.example.unihub.entity.QuizAnswer;
import org.example.unihub.entity.QuizQuestion;
import org.example.unihub.entity.StudentProfile;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.QuizAnswerRepository;
import org.example.unihub.repository.QuizQuestionRepository;
import org.example.unihub.repository.StudentProfileRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAnswerService {
    private final QuizAnswerRepository answerRepository;
    private final QuizQuestionRepository questionRepository;
    private final StudentProfileRepository studentRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public QuizAnswerResponseDTO submitAnswer(Long questionId, Long userId, QuizAnswerRequest answerRequest) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a student and is enrolled in the class
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            throw new BusinessException("Only students can submit quiz answers");
        }

        if (!question.getQuiz().getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You are not enrolled in this class");
        }

        StudentProfile student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found"));

        boolean isCorrect = answerRequest.getAnswer().equals(question.getCorrectAnswer());
        int pointsEarned = isCorrect ? question.getPoints() : 0;

        QuizAnswer answer = mapper.toQuizAnswer(answerRequest);
        answer.setQuestion(question);
        answer.setStudent(student);
        answer.setIsCorrect(isCorrect);
        answer.setPointsEarned(pointsEarned);

        QuizAnswer savedAnswer = answerRepository.save(answer);
        return mapper.toQuizAnswerResponseDTO(savedAnswer);
    }

    @Transactional(readOnly = true)
    public List<QuizAnswerResponseDTO> getAnswersByQuestionId(Long questionId, Long userId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !question.getQuiz().getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to these answers");
        }

        return answerRepository.findByQuestionId(questionId)
                .stream()
                .map(mapper::toQuizAnswerResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizAnswerResponseDTO> getAnswersByStudentId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            throw new BusinessException("Only students can view their own answers");
        }

        StudentProfile student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found"));

        return answerRepository.findByStudentId(student.getId())
                .stream()
                .map(mapper::toQuizAnswerResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizAnswerResponseDTO getAnswerById(Long id, Long userId) {
        QuizAnswer answer = answerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Answer not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or the student who submitted the answer
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !answer.getStudent().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have access to this answer");
        }

        return mapper.toQuizAnswerResponseDTO(answer);
    }
} 