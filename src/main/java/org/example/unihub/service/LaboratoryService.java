package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.LaboratoryRequest;
import org.example.unihub.dto.LaboratoryDTO;
import org.example.unihub.entity.ClassEntity;
import org.example.unihub.entity.Laboratory;
import org.example.unihub.errors.BusinessException;
import org.example.unihub.repository.ClassRepository;
import org.example.unihub.repository.LaboratoryRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaboratoryService {
    private final LaboratoryRepository laboratoryRepository;
    private final ClassRepository classRepository;
    private final Mapper mapper;

    @Transactional
    public LaboratoryDTO createLaboratory(Long classId, Long userId, LaboratoryRequest createLaboratoryRequest) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException("Class not found"));

        if (!classEntity.getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("Only the professor of this class can create laboratories");
        }

        Laboratory laboratory = new Laboratory();
        laboratory.setTitle(createLaboratoryRequest.getTitle());
        laboratory.setNumber(createLaboratoryRequest.getNumber());
        laboratory.setDescription(createLaboratoryRequest.getDescription());
        laboratory.setClassEntity(classEntity);

        Laboratory savedLaboratory = laboratoryRepository.save(laboratory);
        return mapper.toLaboratoryDTO(savedLaboratory);
    }

    @Transactional(readOnly = true)
    public List<LaboratoryDTO> getLaboratoriesByClassId(Long classId) {
        return laboratoryRepository.findByClassEntityId(classId)
                .stream()
                .map(mapper::toLaboratoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LaboratoryDTO getLaboratoryById(Long id) {
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));
        return mapper.toLaboratoryDTO(laboratory);
    }

    @Transactional
    public LaboratoryDTO updateLaboratory(Long id, Long userId, LaboratoryRequest LaboratoryRequest) {
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));

        if (!laboratory.getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("Only the professor of this class can update laboratories");
        }

        laboratory.setTitle(LaboratoryRequest.getTitle());
        laboratory.setNumber(LaboratoryRequest.getNumber());
        laboratory.setDescription(LaboratoryRequest.getDescription());

        Laboratory updatedLaboratory = laboratoryRepository.save(laboratory);
        return mapper.toLaboratoryDTO(updatedLaboratory);
    }

    @Transactional
    public void deleteLaboratory(Long id, Long userId) {
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Laboratory not found"));

        // Verify that the user is the professor of this class
        if (!laboratory.getClassEntity().getProfessor().getUser().getId().equals(userId)) {
            throw new BusinessException("Only the professor of this class can delete laboratories");
        }

        laboratoryRepository.delete(laboratory);
    }


} 