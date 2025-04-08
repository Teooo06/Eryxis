package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContiRepository extends JpaRepository<Conti, String> {
    Conti findByIBAN (String iban);
    List<Conti> findByUtente (Utenti utente);
    List<Conti> findByConsulente (Utenti consulente);
}