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

    public Tickets findByIdTicket(int idTicket) {
        return ticketsRepository.findByIdTicket(idTicket);
    }

    public List<Tickets> findByUtente(Utenti utente) {
        return ticketsRepository.findByUtente(utente);
    }


    // funzioni per modifica o rimozione
    public void save(Tickets tickets) {
        ticketsRepository.save(tickets);
    }

    public Tickets getSave(Tickets tickets) {
        return ticketsRepository.save(tickets);
    }

    public void deleteByIdTicket(int idTicket) {
        ticketsRepository.deleteById(idTicket);
    }

    public void deleteByTicket(Tickets tickets) {
        ticketsRepository.delete(tickets);
    }
}
