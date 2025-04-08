package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Transazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransazioniRepository extends JpaRepository<Transazioni, Integer> {
    Transazioni findByIdTransazione (int idTransazione);
    List<Transazioni> findByConto (Conti conto);
}