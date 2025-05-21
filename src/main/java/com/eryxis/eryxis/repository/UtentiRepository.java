package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Permessi;
import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtentiRepository extends JpaRepository<Utenti, Integer> {
    boolean existsByMail (String mail);
    Utenti findByIdUtente (int idUtente);
    Utenti findByMailAndPassword (String mail, String password);
    Utenti findByMail (String mail);
    List<Utenti> findByPermesso (Permessi permesso);
    Utenti findByIdUtenteAndPermesso (int idUtente, Permessi permesso);
}