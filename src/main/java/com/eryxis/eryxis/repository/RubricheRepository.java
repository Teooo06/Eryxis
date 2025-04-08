package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Rubriche;
import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RubricheRepository extends JpaRepository<Rubriche, Integer> {
    Rubriche findByIdContatto(int idContatto);
    List<Rubriche> findByUtente (Utenti utente);
}