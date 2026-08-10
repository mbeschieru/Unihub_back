package org.example.unihub.repository;

import org.example.unihub.entity.ProfessorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorProfileRepository extends JpaRepository<ProfessorProfile, Long> {
    Optional<ProfessorProfile> findByUserId(Long userId);
} 