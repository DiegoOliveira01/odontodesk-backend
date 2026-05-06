package com.diego.odontodesk.service;

import com.diego.odontodesk.dto.request.PatientRequestDTO;
import com.diego.odontodesk.dto.response.PatientResponseDTO;
import com.diego.odontodesk.exception.BusinessException;
import com.diego.odontodesk.exception.ResourceNotFoundException;
import com.diego.odontodesk.model.Patient;
import com.diego.odontodesk.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    // @Transactional(readOnly = true) - abre uma transação de leitura
    // o banco pode otimizar queries de leitura quando sabe que não haverá escrita
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findAll(){
        return patientRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO findById(Long id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
        return toResponseDTO(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findByName(String name){
        return patientRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // @Transactional — abre transação de escrita
    // Se qualquer exception for lançada, o Spring faz rollback automaticamente
    @Transactional
    public PatientResponseDTO create(PatientRequestDTO dto){

        // Valida CPF duplicado
        if (patientRepository.existsByCpf(dto.getCpf())){
            throw new BusinessException("CPF já cadastrado: " + dto.getCpf());
        }

        // Valida Email duplicado (Só se foi informado)
        if (dto.getEmail() != null && patientRepository.existsByEmail(dto.getEmail())){
            throw new BusinessException("Email já cadastrado " + dto.getEmail());
        }

        Patient patient = Patient.builder()
                .name(dto.getName())
                .cpf(dto.getCpf())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .build();

        Patient saved = patientRepository.save(patient);
        return toResponseDTO(saved);
    }

    @Transactional
    public PatientResponseDTO update(Long id, PatientRequestDTO dto){

        // Busca paciente - lança 404 se não existir
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));

        // Valida CPF duplicado — ignora o próprio paciente na verificação
        if (!patient.getCpf().equals(dto.getCpf()) &&
                patientRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado: " + dto.getCpf());
        }

        // Valida email duplicado — ignora o próprio paciente
        if (dto.getEmail() != null &&
                !dto.getEmail().equals(patient.getEmail()) &&
                patientRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        // Atualiza os campos
        patient.setName(dto.getName());
        patient.setCpf(dto.getCpf());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setBirthDate(dto.getBirthDate());
        patient.setAddress(dto.getAddress());

        // O @PreUpdate da entidade cuida do updatedAt automaticamente
        // O save() aqui faz UPDATE porque a entidade já tem id
        Patient updated = patientRepository.save(patient);
        return toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id){
        if (!patientRepository.existsById(id)){
            throw new ResourceNotFoundException("Paciente", id);
        }
        patientRepository.deleteById(id);
    }


    // Método privado de conversão — evita repetição em todos os métodos acima
    // "this::toResponseDTO" é uma method reference — equivale a p -> toResponseDTO(p)
    private PatientResponseDTO toResponseDTO(Patient patient){
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .cpf(patient.getCpf())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .birthDate(patient.getBirthDate())
                .address(patient.getAddress())
                .createdAt(patient.getCreatedAt())
                .build();
    }

}
