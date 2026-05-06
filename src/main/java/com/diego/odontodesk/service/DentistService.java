package com.diego.odontodesk.service;

import com.diego.odontodesk.dto.request.DentistRequestDTO;
import com.diego.odontodesk.dto.response.DentistResponseDTO;
import com.diego.odontodesk.exception.BusinessException;
import com.diego.odontodesk.exception.ResourceNotFoundException;
import com.diego.odontodesk.model.Dentist;
import com.diego.odontodesk.repository.DentistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DentistService {

    private final DentistRepository dentistRepository;

    @Transactional(readOnly = true)
    public List<DentistResponseDTO> findAll(){
        return dentistRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DentistResponseDTO findById(Long id){
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentista", id));
        return toResponseDTO(dentist);
    }

    @Transactional(readOnly = true)
    public List<DentistResponseDTO> findByName(String name){
        return dentistRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DentistResponseDTO> findBySpecialty(String specialty){
        return dentistRepository.findBySpecialtyIgnoreCase(specialty)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public DentistResponseDTO create(DentistRequestDTO dto){

        if (dentistRepository.existsByCro(dto.getCro())){
            throw new BusinessException("CRO já cadastrado: " + dto.getCro());
        }

        if (dto.getEmail() != null && dentistRepository.existsByEmail(dto.getEmail())){
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        Dentist dentist = Dentist.builder()
                .name(dto.getName())
                .cro(dto.getCro())
                .specialty(dto.getSpecialty())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();

        return toResponseDTO(dentistRepository.save(dentist));
    }

    @Transactional
    public DentistResponseDTO update(Long id, DentistRequestDTO dto){
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentista", id));

        if (!dentist.getCro().equals(dto.getCro()) && dentistRepository.existsByCro(dto.getCro())){
            throw new BusinessException("CRO já cadastrado: " + dto.getCro());
        }

        if (dto.getEmail() != null &&
                !dto.getEmail().equals(dentist.getEmail()) && dentistRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        dentist.setName(dto.getName());
        dentist.setCro(dto.getCro());

        dentist.setSpecialty(dto.getSpecialty());
        dentist.setPhone(dto.getPhone());
        dentist.setEmail(dto.getEmail());

        return toResponseDTO(dentistRepository.save(dentist));
    }

    @Transactional
    public void delete(Long id){
        if (!dentistRepository.existsById(id)){
            throw new ResourceNotFoundException("Dentista", id);
        }
        dentistRepository.deleteById(id);
    }


    // Método privado de conversão — evita repetição em todos os métodos acima
    // "this::toResponseDTO" é uma method reference — equivale a p -> toResponseDTO(p)
    private DentistResponseDTO toResponseDTO(Dentist dentist) {
        return DentistResponseDTO.builder()
                .id(dentist.getId())
                .name(dentist.getName())
                .cro(dentist.getCro())
                .specialty(dentist.getSpecialty())
                .phone(dentist.getPhone())
                .email(dentist.getEmail())
                .createdAt(dentist.getCreatedAt())
                .build();
    }
}
