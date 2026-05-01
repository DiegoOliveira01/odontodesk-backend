package com.diego.odontodesk.repository;

import com.diego.odontodesk.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Spring traduz para: SELECT * FROM patients WHERE cpf=?
    Optional<Patient> findByCpf(String cpf);

    // SELECT * FROM patients WHERE email = ?
    Optional<Patient> findByEmail(String email);

    // SELECT * FROM patients WHERE LOWER(name) LIKE LOWER('%name%')
    List<Patient> findByNameContainingIgnoreCase(String name);

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
