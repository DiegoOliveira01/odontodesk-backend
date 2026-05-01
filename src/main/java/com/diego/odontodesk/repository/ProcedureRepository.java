package com.diego.odontodesk.repository;

import com.diego.odontodesk.model.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    List<Procedure> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}
