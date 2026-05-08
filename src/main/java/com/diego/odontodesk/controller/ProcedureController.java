package com.diego.odontodesk.controller;

import com.diego.odontodesk.dto.request.ProcedureRequestDTO;
import com.diego.odontodesk.dto.response.PatientResponseDTO;
import com.diego.odontodesk.dto.response.ProcedureResponseDTO;
import com.diego.odontodesk.service.ProcedureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/procedures")
@RequiredArgsConstructor
public class ProcedureController {

    private final ProcedureService procedureService;

    @GetMapping
    public ResponseEntity<List<ProcedureResponseDTO>> findAll(
            @RequestParam(required = false) String name){

        if (name != null && !name.isBlank()){
            return ResponseEntity.ok(procedureService.findByName(name));
        }
        return ResponseEntity.ok(procedureService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcedureResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(procedureService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProcedureResponseDTO> create(
            @Valid @RequestBody ProcedureRequestDTO dto){

        ProcedureResponseDTO created = procedureService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcedureResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody ProcedureRequestDTO dto){

        return ResponseEntity.ok(procedureService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        procedureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
