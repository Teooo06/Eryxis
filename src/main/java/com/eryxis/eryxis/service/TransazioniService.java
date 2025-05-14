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

    /**
     * Recupera una transazione a partire dall'ID specificato.
     *
     * @param idTransazioni L'ID della transazione da cercare.
     * @return La transazione corrispondente, se trovata.
     */
    public Transazioni findByIdTransazione(int idTransazioni) {
        return transazioniRepository.findByIdTransazione(idTransazioni);
    }

    /**
     * Restituisce tutte le transazioni associate a un determinato conto.
     *
     * @param conto Il conto per cui recuperare le transazioni.
     * @return Lista di transazioni collegate al conto.
     */
    public List<Transazioni> findByConto(Conti conto) {
        return transazioniRepository.findByConto(conto);
    }


    // funzioni per aggiornare o rimuovere
    /**
     * Salva una transazione nel repository.
     *
     * @param transazioni La transazione da salvare.
     */
    public void save(Transazioni transazioni) {
        transazioniRepository.save(transazioni);
    }

    /**
     * Salva una transazione e la restituisce.
     *
     * @param transazioni La transazione da salvare.
     * @return La transazione salvata.
     */
    public Transazioni getSave(Transazioni transazioni) {
        return transazioniRepository.save(transazioni);
    }

    /**
     * Elimina una transazione in base all'ID specificato.
     *
     * @param idTransazioni L'ID della transazione da eliminare.
     */
    public void deleteByIdTransazioni(int idTransazioni) {
        transazioniRepository.deleteById(idTransazioni);
    }

    /**
     * Elimina una transazione specifica dal repository.
     *
     * @param transazioni La transazione da eliminare.
     */
    public void deleteByTransazioni(Transazioni transazioni) {
        transazioniRepository.delete(transazioni);
    }
}
