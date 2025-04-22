package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.repository.CarteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarteService {
    @Autowired
    private CarteRepository carteRepository;

    public Carte findByNumeroCarta(String numeroCarta) {
        return carteRepository.findByNumeroCarta(numeroCarta);
    }

    public Carte findByNumeroCartaAndCVVAndDataScadenza(String numeroCarta, String CVV, java.sql.Date dataScadenza) {
        return carteRepository.findByNumeroCartaAndCVVAndDataScadenza(numeroCarta, CVV, dataScadenza);
    }

    public List<Carte> findByConto(Conti conto) {
        return carteRepository.findByConto(conto);
    }

    // funzioni di base per aggiungere o rimuovere
    public void save(Carte carta) {
        carteRepository.save(carta);
    }

    public Carte getSave(Carte carta) {
        return carteRepository.save(carta);
    }

    public void deleteById(String numeroCarta) {
        carteRepository.deleteById(numeroCarta);
    }

    public void deleteByCarta(Carte carta) {
        carteRepository.delete(carta);
    }

}
