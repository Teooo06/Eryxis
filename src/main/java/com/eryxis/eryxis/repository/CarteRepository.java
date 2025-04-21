package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteRepository extends JpaRepository<Carte, String> {
    Carte findByNumeroCarta (String numeroCarta);
    Carte findByNumeroCartaAndCVVAndDataScadenza (String numeroCarta, String CVV, java.sql.Date dataScadenza);
    List<Carte> findByConto (Conti conto);
    List<Carte> findAllByIban(String IBAN);
}