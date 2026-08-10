package org.example.unihub.repository;

import org.example.unihub.entity.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {
    List<TaskSubmission> findByTaskId(Long taskId);
    List<TaskSubmission> findByStudentId(Long studentId);
    List<TaskSubmission> findByTaskIdAndStudentId(Long taskId, Long studentId);
} 