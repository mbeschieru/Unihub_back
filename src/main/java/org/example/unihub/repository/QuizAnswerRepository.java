package org.example.unihub.repository;

import org.example.unihub.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findByQuestionId(Long questionId);
    List<QuizAnswer> findByStudentId(Long studentId);
    List<QuizAnswer> findByQuestionIdAndStudentId(Long questionId, Long studentId);
} 