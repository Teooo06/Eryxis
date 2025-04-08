package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Logs;
import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepository extends JpaRepository<Logs, Integer> {
    Logs findByIdLog (int idLog);
    List<Logs> findByUtente (Utenti utente);
}