package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtentiRepository extends JpaRepository<Utenti, Integer> {
    boolean existsByMail (String mail);
    Utenti findByIdUtente (int idUtente);
    Utenti findByMailAndPassword (String mail, String password);

}