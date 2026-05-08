package com.diego.odontodesk.controller;

import com.diego.odontodesk.dto.request.DentistRequestDTO;
import com.diego.odontodesk.dto.response.DentistResponseDTO;
import com.diego.odontodesk.service.DentistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/dentists")
@RequiredArgsConstructor
public class DentistController {

    private final DentistService dentistService;

    @GetMapping
    public ResponseEntity<List<DentistResponseDTO>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty){

        // Filtro de especialidade tem prioridade sobre nome
        if (specialty != null && !specialty.isBlank()){
            return ResponseEntity.ok(dentistService.findBySpecialty(specialty));
        }
        if (name!= null && !name.isBlank()){
            return ResponseEntity.ok(dentistService.findByName(name));
        }
        return ResponseEntity.ok(dentistService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DentistResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(dentistService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DentistResponseDTO> create(
            @Valid @RequestBody DentistRequestDTO dto){

        DentistResponseDTO created = dentistService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DentistResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody DentistRequestDTO dto){
        return ResponseEntity.ok(dentistService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        dentistService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
