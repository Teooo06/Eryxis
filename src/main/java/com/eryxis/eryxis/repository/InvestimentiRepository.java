package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Investimenti;
import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestimentiRepository extends JpaRepository<Investimenti, String> {
    Investimenti findByIdInvestimento(int idInvestimento);
    List<Investimenti> findByUtente (Utenti utente);
    Investimenti findByUtenteAndSymbol (Utenti utente, String symbol);
    void deleteByIdInvestimento(int idInvestimenti);
}