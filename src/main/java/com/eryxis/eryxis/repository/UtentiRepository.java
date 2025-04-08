package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtentiRepository extends JpaRepository<Utenti, Integer> {
    Utenti findBYIdUtente (int idUtente);
    Utenti findByMailAndPassword (String mail, String password);

}