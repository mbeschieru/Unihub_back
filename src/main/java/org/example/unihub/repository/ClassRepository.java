package org.example.unihub.repository;

import org.example.unihub.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByProfessorId(Long professorId);
    List<ClassEntity> findByStudentsId(Long studentId);
    Optional<ClassEntity> findByInviteUrl(String inviteUrl);
} 