package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseUserProfileDTO {
    private String university;
    private String specialization;
    private boolean emailNotificationsEnabled;
} 