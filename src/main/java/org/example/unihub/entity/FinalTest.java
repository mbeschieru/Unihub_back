package org.example.unihub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "final_tests")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FinalTest extends Assessment {
    @OneToMany(mappedBy = "finalTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestQuestion> questions = new ArrayList<>();
} 