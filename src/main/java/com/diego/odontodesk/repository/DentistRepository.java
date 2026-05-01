package com.diego.odontodesk.repository;

import com.diego.odontodesk.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {

    Optional<Dentist> findByCro(String cro);

    List<Dentist> findByNameContainingIgnoreCase(String name);

    List<Dentist> findBySpecialIgnoreCase(String speciality);

    boolean existsByCro(String cro);
    boolean existsByEmail(String email);
}
