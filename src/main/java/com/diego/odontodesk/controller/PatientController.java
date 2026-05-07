package com.diego.odontodesk.controller;

import com.diego.odontodesk.dto.request.PatientRequestDTO;
import com.diego.odontodesk.dto.response.PatientResponseDTO;
import com.diego.odontodesk.service.PatientService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> finddAll(
            // Parâmetro opcional de busca por nome
            // GET /api/patients?name=joão → filtra por nome
            // GET /api/patients → retorna todos
            @RequestParam(required = false) String name){
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(patientService.findByName(name));
        }
        return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(patientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> create(@Valid @RequestBody PatientRequestDTO dto){

        PatientResponseDTO created = patientService.create(dto);

        // Boas práticas REST: POST retorna 201 Created
        // com o header Location apontando para o novo recurso
        // Ex: Location: /api/patients/42

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDTO dto){

        return ResponseEntity.ok(patientService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        patientService.delete(id);
        // 204 No Content — deletou com sucesso, sem corpo na resposta
        return ResponseEntity.noContent().build();
    }

}
