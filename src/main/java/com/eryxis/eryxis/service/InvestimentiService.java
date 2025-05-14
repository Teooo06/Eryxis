package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Investimenti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.InvestimentiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestimentiService {
    @Autowired
    private InvestimentiRepository investimentiRepository;

    /**
     * Recupera un investimento a partire dal codice ISIN specificato.
     *
     * @param isin Il codice ISIN dell'investimento.
     * @return L'investimento corrispondente, se trovato.
     */
    public Investimenti findByISIN(String isin) {
        return investimentiRepository.findByISIN(isin);
    }

    /**
     * Restituisce la lista degli investimenti associati a un determinato utente.
     *
     * @param utente L'utente per cui recuperare gli investimenti.
     * @return Lista degli investimenti collegati all'utente.
     */
    public List<Investimenti> findByUtente(Utenti utente) {
        return investimentiRepository.findByUtente(utente);
    }


    // funzioni di base per aggiungere o rimuovere
    /**
     * Salva un investimento nel repository.
     *
     * @param investimenti L'investimento da salvare.
     */
    public void save(Investimenti investimenti) {
        investimentiRepository.save(investimenti);
    }

    /**
     * Salva un investimento e lo restituisce.
     *
     * @param investimenti L'investimento da salvare.
     * @return L'investimento salvato.
     */
    public Investimenti getSave(Investimenti investimenti) {
        return investimentiRepository.save(investimenti);
    }

    /**
     * Elimina un investimento in base al codice ISIN.
     *
     * @param isin Il codice ISIN dell'investimento da eliminare.
     */
    public void deleteById(String isin) {
        investimentiRepository.deleteById(isin);
    }

    /**
     * Elimina un investimento specifico.
     *
     * @param investimenti L'investimento da eliminare.
     */
    public void deleteByInvestimenti(Investimenti investimenti) {
        investimentiRepository.delete(investimenti);
    }
}
