package com.diego.odontodesk.service;

import com.diego.odontodesk.dto.request.ProcedureRequestDTO;
import com.diego.odontodesk.dto.response.ProcedureResponseDTO;
import com.diego.odontodesk.exception.BusinessException;
import com.diego.odontodesk.exception.ResourceNotFoundException;
import com.diego.odontodesk.model.Procedure;
import com.diego.odontodesk.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcedureService {

    private final ProcedureRepository procedureRepository;

    @Transactional(readOnly = true)
    public List<ProcedureResponseDTO> findAll(){
        return procedureRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProcedureResponseDTO findById(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento", id));
        return toResponseDTO(procedure);
    }

    @Transactional(readOnly = true)
    public List<ProcedureResponseDTO> findByName(String name) {
        return procedureRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProcedureResponseDTO create(ProcedureRequestDTO dto){
        if (procedureRepository.existsByName(dto.getName())){
            throw new BusinessException("Procedimento já cadastrado: " + dto.getName());
        }

        Procedure procedure = Procedure.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .estimatedDurationMinutes(dto.getEstimatedDurationMinutes())
                .price(dto.getPrice())
                .build();

        return toResponseDTO(procedureRepository.save(procedure));
    }

    @Transactional
    public ProcedureResponseDTO update(Long id, ProcedureRequestDTO dto){
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Procedimento já cadastrado: " + dto.getName()));

        if (!procedure.getName().equals(dto.getName()) && procedureRepository.existsByName(dto.getName())){
            throw new BusinessException("Procedimento já cadastrado: " + dto.getName());
        }

        procedure.setName(dto.getName());
        procedure.setDescription(dto.getDescription());
        procedure.setEstimatedDurationMinutes(dto.getEstimatedDurationMinutes());
        procedure.setPrice(dto.getPrice());

        return toResponseDTO(procedureRepository.save(procedure));
    }

    @Transactional
    public void delete(Long id){
        if (!procedureRepository.existsById(id)){
            throw new ResourceNotFoundException("Procedimento", id);
        }
        procedureRepository.deleteById(id);
    }

    private ProcedureResponseDTO toResponseDTO(Procedure procedure){
        return ProcedureResponseDTO.builder()
                .id(procedure.getId())
                .name(procedure.getName())
                .description(procedure.getDescription())
                .estimatedDurationMinutes(procedure.getEstimatedDurationMinutes())
                .price(procedure.getPrice())
                .build();
    }
}
