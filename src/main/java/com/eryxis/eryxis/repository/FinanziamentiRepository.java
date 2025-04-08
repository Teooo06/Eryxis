package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Finanziamenti;
import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanziamentiRepository extends JpaRepository<Finanziamenti, Integer> {
    Finanziamenti findByIdFinanziamento (int idFinanziamento);
    List<Finanziamenti> findByUtente (Utenti utente);
}