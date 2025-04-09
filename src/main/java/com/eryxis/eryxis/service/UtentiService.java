package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.UtentiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtentiService {
    @Autowired
    private UtentiRepository utentiRepository;

    public Utenti findByIdUtente(int idUtente) {
        return utentiRepository.findBYIdUtente(idUtente);
    }

    public Utenti findByMailAndPassword(String mail, String password) {
        return utentiRepository.findByMailAndPassword(mail, password);
    }


    // funzioni di base per aggiungere o rimuovere
    public void save(Utenti utente) {
        utentiRepository.save(utente);
    }

    public void deleteById(int idUtente) {
        utentiRepository.deleteById(idUtente);
    }

    public void deleteByUtente(Utenti utente) {
        utentiRepository.delete(utente);
    }
}
