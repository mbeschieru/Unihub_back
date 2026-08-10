package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TaskSubmissionRequest;
import org.example.unihub.dto.TaskSubmissionResponseDTO;
import org.example.unihub.entity.StudentProfile;
import org.example.unihub.entity.Task;
import org.example.unihub.entity.TaskSubmission;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.StudentProfileRepository;
import org.example.unihub.repository.TaskRepository;
import org.example.unihub.repository.TaskSubmissionRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {
    private final TaskSubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final StudentProfileRepository studentRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public TaskSubmissionResponseDTO submitTask(Long taskId, Long userId, TaskSubmissionRequest submissionRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a student and is enrolled in the class
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            throw new BusinessException("Only students can submit tasks");
        }

        if (!task.getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You are not enrolled in this class");
        }

        StudentProfile student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found"));

        TaskSubmission submission = mapper.toTaskSubmission(submissionRequest);
        submission.setTask(task);
        submission.setStudent(student);
        submission.setSubmissionDate(LocalDateTime.now());

        TaskSubmission savedSubmission = submissionRepository.save(submission);
        return mapper.toTaskSubmissionResponseDTO(savedSubmission);
    }

    @Transactional
    public TaskSubmissionResponseDTO gradeSubmission(Long submissionId, Integer grade, String feedback, Long userId) {
        TaskSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("Submission not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who owns this task
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !submission.getTask().getLaboratory().getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to grade this submission");
        }

        submission.setGrade(grade);
        submission.setFeedback(feedback);

        TaskSubmission updatedSubmission = submissionRepository.save(submission);
        return mapper.toTaskSubmissionResponseDTO(updatedSubmission);
    }

    @Transactional(readOnly = true)
    public List<TaskSubmissionResponseDTO> getSubmissionsByTaskId(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) )
            throw new BusinessException("You don't have access to these submissions");


        List<TaskSubmission> submissions = submissionRepository.findByTaskId(taskId);

        return submissions.stream()
                .map(mapper::toTaskSubmissionResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<TaskSubmissionResponseDTO> getMySubmissionsByTaskId(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));


        List<TaskSubmission> submissions = submissionRepository.findByTaskId(taskId);

        return submissions.stream()
                .filter( submission -> submission.getStudent().getUser().getId().equals(userId))
                .map(mapper::toTaskSubmissionResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskSubmissionResponseDTO> getSubmissionsByStudentId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            throw new BusinessException("Only students can view their own submissions");
        }

        StudentProfile student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found"));

        return submissionRepository.findByStudentId(student.getUser().getId())
                .stream()
                .map(mapper::toTaskSubmissionResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskSubmissionResponseDTO getSubmissionById(Long id, Long userId) {
        TaskSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Submission not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or the student who submitted
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !submission.getStudent().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have access to this submission");
        }

        return mapper.toTaskSubmissionResponseDTO(submission);
    }
    @Transactional(readOnly = true)
    public List<String> getStudentsByTaskId(Long id , Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new BusinessException("User not found"));

        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR"))) {
            throw new BusinessException("You don't have access to this endpoint");
        }

        return submissionRepository.findByTaskId(id)
                .stream()
                .map(submission ->  {
                    StudentProfile studentProfile = submission.getStudent();
                    User studentUser = studentProfile.getUser();
                    return studentUser.getFirstName() + " " + studentUser.getLastName();
                })
                .distinct()
                .toList();


    }
} 