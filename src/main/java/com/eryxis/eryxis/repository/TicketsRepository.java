package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Tickets;
import com.eryxis.eryxis.model.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Integer> {
    Tickets findByIdTicket (int idTicket);
    List<Tickets> findByUtente (Utenti utente);

    /*
     * Lo stato può essere:
     * 1 = aperto e in attesa
     * 2 = in lavorazione
     * 3 = chiuso
     */
    List<Tickets> findByUtenteAndStato(Utenti utente, short stato);
}