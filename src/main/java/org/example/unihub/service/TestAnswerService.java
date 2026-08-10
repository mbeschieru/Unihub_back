package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TestAnswerDTO;
import org.example.unihub.dto.TestAnswerRequest;
import org.example.unihub.dto.TestAnswerResponseDTO;
import org.example.unihub.entity.StudentProfile;
import org.example.unihub.entity.TestAnswer;
import org.example.unihub.entity.TestQuestion;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.StudentProfileRepository;
import org.example.unihub.repository.TestAnswerRepository;
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
public class TestAnswerService {
    private final TestAnswerRepository answerRepository;
    private final TestQuestionRepository questionRepository;
    private final StudentProfileRepository studentRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public TestAnswerResponseDTO submitAnswer(Long questionId, Long userId, TestAnswerRequest answerRequest) {
        TestQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a student and is enrolled in the class
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            throw new BusinessException("Only students can submit test answers");
        }

        if (!question.getFinalTest().getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You are not enrolled in this class");
        }

        StudentProfile student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found"));

        boolean isCorrect = answerRequest.getAnswer().equals(question.getCorrectAnswer());
        int pointsEarned = isCorrect ? question.getPoints() : 0;

        TestAnswer answer = mapper.toTestAnswer(answerRequest);
        answer.setQuestion(question);
        answer.setStudent(student);
        answer.setIsCorrect(isCorrect);
        answer.setPointsEarned(pointsEarned);

        TestAnswer savedAnswer = answerRepository.save(answer);
        return mapper.toTestAnswerResponseDTO(savedAnswer);
    }

    @Transactional(readOnly = true)
    public List<TestAnswerResponseDTO> getAnswersByQuestionId(Long questionId, Long userId) {
        TestQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException("Question not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !question.getFinalTest().getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to these answers");
        }

        return answerRepository.findByQuestionId(questionId)
                .stream()
                .map(mapper::toTestAnswerResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestAnswerResponseDTO> getAnswersByStudentId(Long userId) {
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
                .map(mapper::toTestAnswerResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestAnswerResponseDTO getAnswerById(Long id, Long userId) {
        TestAnswer answer = answerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Answer not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or the student who submitted the answer
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !answer.getStudent().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have access to this answer");
        }

        return mapper.toTestAnswerResponseDTO(answer);
    }

    @Transactional(readOnly = true)
    public List<TestAnswerResponseDTO> getAnswersByTestId(Long testId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a professor
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR"))) {
            throw new BusinessException("Only professors can view all answers for a test");
        }

        // Get all questions for the test
        List<TestQuestion> questions = questionRepository.findByFinalTestId(testId);
        
        // Get all answers for these questions
        List<TestAnswer> answers = questions.stream()
                .flatMap(question -> answerRepository.findByQuestionId(question.getId()).stream())
                .collect(Collectors.toList());

        return answers.stream()
                .map(mapper::toTestAnswerResponseDTO)
                .collect(Collectors.toList());
    }
} 