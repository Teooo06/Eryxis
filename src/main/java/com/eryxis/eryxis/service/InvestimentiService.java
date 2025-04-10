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

    public Investimenti findByISIN(String isin) {
        return investimentiRepository.findByISIN(isin);
    }

    public List<Investimenti> findByUtente(Utenti utente) {
        return investimentiRepository.findByUtente(utente);
    }


    // funzioni di base per aggiungere o rimuovere
    public void save(Investimenti investimenti) {
        investimentiRepository.save(investimenti);
    }

    public Investimenti getSave(Investimenti investimenti) {
        return investimentiRepository.save(investimenti);
    }

    public void deleteById(String isin) {
        investimentiRepository.deleteById(isin);
    }

    public void deleteByInvestimenti(Investimenti investimenti) {
        investimentiRepository.delete(investimenti);
    }
}
