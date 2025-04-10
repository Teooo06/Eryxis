package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Transazioni;
import com.eryxis.eryxis.repository.TransazioniRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransazioniService {
    @Autowired
    private TransazioniRepository transazioniRepository;

    public Transazioni findByIdTransazione(int idTransazioni) {
        return transazioniRepository.findByIdTransazione(idTransazioni);
    }

    public List<Transazioni> findByConto(Conti conto) {
        return transazioniRepository.findByConto(conto);
    }


    // funzioni per aggiornare o rimuovere
    public void save(Transazioni transazioni) {
        transazioniRepository.save(transazioni);
    }

    public Transazioni getSave(Transazioni transazioni) {
        return transazioniRepository.save(transazioni);
    }

    public void deleteByIdTransazioni(int idTransazioni) {
        transazioniRepository.deleteById(idTransazioni);
    }

    public void deleteByTransazioni(Transazioni transazioni) {
        transazioniRepository.delete(transazioni);
    }
}
