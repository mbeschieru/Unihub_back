package org.example.unihub.repository;

import org.example.unihub.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    List<Laboratory> findByClassEntityId(Long classId);
} 