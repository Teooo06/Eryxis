package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.CarteRepository;
import com.eryxis.eryxis.repository.ContiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarteService {
    @Autowired
    private CarteRepository carteRepository;

    /**
     * Cerca una carta a partire dal numero della carta.
     *
     * @param numeroCarta Il numero della carta da cercare.
     * @return La carta corrispondente, se trovata.
     */
    public Carte findByNumeroCarta(String numeroCarta) {
        return carteRepository.findByNumeroCarta(numeroCarta);
    }

    /**
     * Cerca una carta a partire da numero, CVV e data di scadenza.
     *
     * @param numeroCarta Il numero della carta.
     * @param CVV Il codice CVV.
     * @param dataScadenza La data di scadenza.
     * @return La carta corrispondente, se trovata.
     */
    public Carte findByNumeroCartaAndCVVAndDataScadenza(String numeroCarta, String CVV, java.sql.Date dataScadenza) {
        return carteRepository.findByNumeroCartaAndCVVAndDataScadenza(numeroCarta, CVV, dataScadenza);
    }

    /**
     * Restituisce tutte le carte associate a un determinato conto.
     *
     * @param conto Il conto associato.
     * @return Lista di carte collegate al conto.
     */
    public List<Carte> findByConto(Conti conto) {
        return carteRepository.findByConto(conto);
    }

    // funzioni di base per aggiungere o rimuovere
    /**
     * Salva una carta nel repository.
     *
     * @param carta La carta da salvare.
     */
    public void save(Carte carta) {
        carteRepository.save(carta);
    }

    /**
     * Salva una carta e la restituisce.
     *
     * @param carta La carta da salvare.
     * @return La carta salvata.
     */
    public Carte getSave(Carte carta) {
        return carteRepository.save(carta);
    }

    /**
     * Elimina una carta in base al numero della carta.
     *
     * @param numeroCarta Il numero della carta da eliminare.
     */
    public void deleteById(String numeroCarta) {
        carteRepository.deleteById(numeroCarta);
    }

    /**
     * Elimina una carta specifica.
     *
     * @param carta La carta da eliminare.
     */
    public void deleteByCarta(Carte carta) {
        carteRepository.delete(carta);
    }

    /**
     * Cerca una carta in base al CVV.
     *
     * @param string Il codice CVV.
     * @return La carta corrispondente, se trovata.
     */
    public Carte findByCVV(String string) {
        return carteRepository.findByCVV(string);
    }

    /**
     * Cerca una carta in base al PIN.
     *
     * @param string Il codice PIN.
     * @return La carta corrispondente, se trovata.
     */
    public Carte findByPIN(String string) {
        return carteRepository.findByPIN(string);
    }

    /**
     * Aggiorna il saldo contabile di tutte le carte il primo giorno del mese.
     * Questa funzione viene eseguita automaticamente ogni mese.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateSaldoContabile() {
        List<Carte> allCards = carteRepository.findAll();
        for (Carte card : allCards) {
            card.setSaldoContabile(card.getSaldoDisponibile());
        }
        carteRepository.saveAll(allCards);
        System.out.println("Saldo contabile updated for all cards.");
    }

    public void aggiornaCarta(String numeroCarta, boolean stato) {
        Carte carta = carteRepository.findByNumeroCarta(numeroCarta);
        carta.setStato(stato);
        save(carta);
    }
}
