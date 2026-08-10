package org.example.unihub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "professor_profiles")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorProfile extends BaseUserProfile {
    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "professor_id", nullable = false, unique = true)
    private String professorId;
} 