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
        return utentiRepository.findByIdUtente(idUtente);
    }

    public boolean esisteByMail(String mail) {
        return utentiRepository.existsByMail(mail);
    }

    public Utenti findByMailAndPassword(String mail, String password) {
        return utentiRepository.findByMailAndPassword(mail, password);
    }

    public boolean findByMailAndPasswordBool(String mail, String password) {
        Utenti utente = utentiRepository.findByMailAndPassword(mail, password);
        return utente != null;
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
