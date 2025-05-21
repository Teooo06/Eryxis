package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Tickets;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.TicketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketsService {
    @Autowired
    private TicketsRepository ticketsRepository;

    /**
     * Recupera un ticket a partire dall'ID specificato.
     *
     * @param idTicket L'ID del ticket da cercare.
     * @return Il ticket corrispondente, se trovato.
     */
    public Tickets findByIdTicket(int idTicket) {
        return ticketsRepository.findByIdTicket(idTicket);
    }

    /**
     * Restituisce tutti i ticket associati a un determinato utente.
     *
     * @param utente L'utente per cui recuperare i ticket.
     * @return Lista di ticket collegati all'utente.
     */
    public List<Tickets> findByUtente(Utenti utente) {
        return ticketsRepository.findByUtente(utente);
    }


    // funzioni per modifica o rimozione
    /**
     * Salva un ticket nel repository.
     *
     * @param tickets Il ticket da salvare.
     */
    public void save(Tickets tickets) {
        ticketsRepository.save(tickets);
    }

    /**
     * Salva un ticket e lo restituisce.
     *
     * @param tickets Il ticket da salvare.
     * @return Il ticket salvato.
     */
    public Tickets getSave(Tickets tickets) {
        return ticketsRepository.save(tickets);
    }

    /**
     * Elimina un ticket in base all'ID specificato.
     *
     * @param idTicket L'ID del ticket da eliminare.
     */
    public void deleteByIdTicket(int idTicket) {
        ticketsRepository.deleteById(idTicket);
    }

    /**
     * Elimina un ticket specifico dal repository.
     *
     * @param tickets Il ticket da eliminare.
     */
    public void deleteByTicket(Tickets tickets) {
        ticketsRepository.delete(tickets);
    }

    /**
     * Retrieves all tickets for a specific user where the status is "0".
     *
     * @param utente The user for whom to retrieve the tickets.
     * @return A list of tickets with status "0" for the given user.
     */
    public List<Tickets> findActiveTicketsByUtente(Utenti utente, short stato) {
        return ticketsRepository.findByUtenteAndStato(utente, stato);
    }

    /**
     * Retrieves all tickets from the repository.
     *
     * @return A list of all tickets.
     */
    public List<Tickets> findAll() {
        return ticketsRepository.findAll();
    }

}
