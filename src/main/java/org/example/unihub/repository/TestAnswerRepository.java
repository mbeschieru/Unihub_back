package org.example.unihub.repository;

import org.example.unihub.entity.TestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {
    List<TestAnswer> findByQuestionId(Long questionId);
    List<TestAnswer> findByStudentId(Long studentId);
    List<TestAnswer> findByQuestionIdAndStudentId(Long questionId, Long studentId);
} 