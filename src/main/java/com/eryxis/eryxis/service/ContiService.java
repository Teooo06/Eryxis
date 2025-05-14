package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.ContiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContiService {
    @Autowired
    public ContiRepository contiRepository;

    /**
     * Restituisce l'elenco completo di tutti i conti presenti nel repository.
     *
     * @return Lista di tutti i conti.
     */
    public List<Conti> findAll() {return contiRepository.findAll();}

    /**
     * Cerca un conto a partire dall'IBAN specificato.
     *
     * @param iban L'IBAN del conto da cercare.
     * @return Il conto corrispondente, se trovato.
     */
    public Conti findByIBAN(String iban) {
        return contiRepository.findByIBAN(iban);
    }

    /**
     * Restituisce tutti i conti associati a un determinato utente.
     *
     * @param utente L'utente proprietario dei conti.
     * @return Lista di conti collegati all'utente.
     */
    public List<Conti> findByUtente(Utenti utente) {
        return contiRepository.findByUtente(utente);
    }

    /**
     * Restituisce tutti i conti gestiti da un determinato consulente.
     *
     * @param consulente Il consulente assegnato ai conti.
     * @return Lista di conti associati al consulente.
     */
    public List<Conti> findByConsulente(Utenti consulente) {
        return contiRepository.findByConsulente(consulente);
    }

    // funzioni di base per aggiungere o rimuovere
    /**
     * Salva un conto nel repository.
     *
     * @param conto Il conto da salvare.
     */
    public void save(Conti conto) {
        contiRepository.save(conto);
    }

    /**
     * Salva un conto e lo restituisce.
     *
     * @param conto Il conto da salvare.
     * @return Il conto salvato.
     */
    public Conti getSave(Conti conto) {
        return contiRepository.save(conto);
    }

    /**
     * Elimina un conto dal repository in base al suo IBAN.
     *
     * @param iban L'IBAN del conto da eliminare.
     */
    public void deleteById(String iban) {
        contiRepository.deleteById(iban);
    }

    /**
     * Elimina un conto specifico dal repository.
     *
     * @param conto Il conto da eliminare.
     */
    public void deleteByConto(Conti conto) {
        contiRepository.delete(conto);
    }

}
