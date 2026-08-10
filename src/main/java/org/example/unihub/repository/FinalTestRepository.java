package org.example.unihub.repository;

import org.example.unihub.entity.FinalTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalTestRepository extends JpaRepository<FinalTest, Long> {
    List<FinalTest> findByLaboratoryId(Long laboratoryId);
} 