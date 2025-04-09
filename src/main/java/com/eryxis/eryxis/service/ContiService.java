package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.ContiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContiService {
    @Autowired
    public ContiRepository contiRepository;

    public Conti findByIBAN(String iban) {
        return contiRepository.findByIBAN(iban);
    }

    public List<Conti> findByUtente(Utenti utente) {
        return contiRepository.findByUtente(utente);
    }

    public List<Conti> findByConsulente(Utenti consulente) {
        return contiRepository.findByConsulente(consulente);
    }


    // funzioni di base per aggiungere o rimuovere
    public void save(Conti conto) {
        contiRepository.save(conto);
    }

    public void deleteById(String iban) {
        contiRepository.deleteById(iban);
    }

    public void deleteByConto(Conti conto) {
        contiRepository.delete(conto);
    }

}
