package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.service.CarteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CarteController {
    @Autowired
    private CarteService carteService;

    public Carte cartaNuova(Conti conto, String tipoCarta) {
        Carte carte = new Carte();
        carte.setConto(conto);
        carte.setDataScadenza(new java.sql.Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L)); // Scadenza tra un anno
        // Creo un numero di carta casuale lungo 16 cifre
        StringBuilder numeroCarta = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
            numeroCarta.append(randomDigit);
        }
        //Controllo che il numero di carta non esista già
        while (carteService.findByNumeroCarta(numeroCarta.toString()) != null) {
            numeroCarta.setLength(0); // Pulisci il StringBuilder
            for (int i = 0; i < 16; i++) {
                int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
                numeroCarta.append(randomDigit);
            }
        }
        carte.setNumeroCarta(numeroCarta.toString());
        // Creo un codice CVV casuale lungo 3 cifre
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
            cvv.append(randomDigit);
        }
        //Controllo che il CVV non esista già
        while (carteService.findByCVV(cvv.toString()) != null) {
            cvv.setLength(0); // Pulisci il StringBuilder
            for (int i = 0; i < 3; i++) {
                int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
                cvv.append(randomDigit);
            }
        }
        carte.setCVV(cvv.toString());
        // Imposto il pin
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
            pin.append(randomDigit);
        }
        // Controllo che il pin non esista già
        while (carteService.findByPIN(pin.toString()) != null) {
            pin.setLength(0); // Pulisci il StringBuilder
            for (int i = 0; i < 5; i++) {
                int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
                pin.append(randomDigit);
            }
        }
        carte.setPIN(pin.toString());

        carte.setTipo(tipoCarta);
        carte.setSaldoContabile(0);
        carte.setSaldoDisponibile(0);

        return carte;
    }
}
