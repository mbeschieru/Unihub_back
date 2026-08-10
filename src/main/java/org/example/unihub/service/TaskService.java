package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.TaskDTO;
import org.example.unihub.dto.TaskRequest;
import org.example.unihub.entity.Laboratory;
import org.example.unihub.entity.Task;
import org.example.unihub.entity.User;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.LaboratoryRepository;
import org.example.unihub.repository.TaskRepository;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public TaskDTO createTask(Long laboratoryId, TaskRequest taskRequest, Long userId) {
        Laboratory laboratory = laboratoryRepository.findById(laboratoryId)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is a professor and has access to this laboratory
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !laboratory.getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to create tasks for this laboratory");
        }

        Task task = mapper.toTask(taskRequest);
        task.setLaboratory(laboratory);

        Task savedTask = taskRepository.save(task);
        return mapper.toTaskDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByLaboratoryId(Long laboratoryId, Long userId) {
        Laboratory laboratory = laboratoryRepository.findById(laboratoryId)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !laboratory.getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this laboratory's tasks");
        }

        return taskRepository.findByLaboratoryId(laboratoryId)
                .stream()
                .map(mapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if user is either the professor or an enrolled student
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) && 
            !task.getLaboratory().getClassEntity().getStudents().stream()
                .anyMatch(student -> student.getUser().getId().equals(userId))) {
            throw new BusinessException("You don't have access to this task");
        }

        return mapper.toTaskDTO(task);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this task
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !task.getLaboratory().getClassEntity().getProfessor().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to update this task");
        }

        Task updatedTask = mapper.toTask(taskDTO);
        updatedTask.setId(task.getId());
        updatedTask.setLaboratory(task.getLaboratory());

        Task savedTask = taskRepository.save(updatedTask);
        return mapper.toTaskDTO(savedTask);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Verify that the user is the professor who created this task
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")) || 
            !task.getLaboratory().getClassEntity().getProfessor().getId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this task");
        }

        taskRepository.deleteById(id);
    }
} 