package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Investimenti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.model.ValoriAzioni;
import com.eryxis.eryxis.repository.InvestimentiRepository;
import com.eryxis.eryxis.repository.ValoriAzioniRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvestimentiService {
    @Autowired
    private InvestimentiRepository investimentiRepository;

    @Autowired
    private ValoriAzioniRepository valoriAzioniRepository;

    /**
     * Recupera un investimento a partire dal codice ISIN specificato.
     *
     * @param idInvestimento Il codice ISIN dell'investimento.
     * @return L'investimento corrispondente, se trovato.
     */
    public Investimenti findByIdInvestimento(String idInvestimento) {
        return investimentiRepository.findByIdInvestimento(idInvestimento);
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
     * @param idInvestimento Il codice ISIN dell'investimento da eliminare.
     */
    public void deleteById(String idInvestimento) {
        investimentiRepository.deleteById(idInvestimento);
    }

    /**
     * Elimina un investimento specifico.
     *
     * @param investimenti L'investimento da eliminare.
     */
    public void deleteByInvestimenti(Investimenti investimenti) {
        investimentiRepository.delete(investimenti);
    }

    /**
     * Calcola la percentuale di investimento in base al prezzo di acquisto e alla quantità.
     *
     * @param idInvestimento Il codice ISIN dell'investimento.
     * @return La percentuale di investimento.
     */
    public int getPercentualeInvestimento(String idInvestimento) {
        Investimenti investimenti = investimentiRepository.findByIdInvestimento(idInvestimento);
        if (investimenti != null) {
            double prezzoAcquisto = investimenti.getPrezzoAcquisto();
            int quantita = investimenti.getQuantita();
            double valoreAcquistoComplessivo = getValoreComplessivo(quantita, prezzoAcquisto);

            LocalDate today = LocalDate.now();
            ValoriAzioni valoreAzione = valoriAzioniRepository.findByIdInvestimentoAndDataValore(investimenti.getIdInvestimento(), today);

            if (valoreAzione != null) {
                double valoreAttuale = valoreAzione.getValore();
                double valoreAttualeComplessivo = valoreAttuale * quantita;

                return (int) ((valoreAttualeComplessivo - valoreAcquistoComplessivo) / valoreAcquistoComplessivo * 100);
            } else {
                // Gestione del caso in cui non ci siano dati per la data odierna
                return 0; // O un valore che indichi l'assenza di dati
            }
        } else {
            return 0; // O gestisci l'errore come preferisci
        }
    }

    /**
     * Calcola la percentuale complessiva di tutti gli investimenti.
     *
     * @return La percentuale complessiva degli investimenti.
     */
    public int getPercentualeComplessiva() {
        List<Investimenti> investimenti = investimentiRepository.findAll();
        double valoreComplessivo = 0;
        double valoreAttualeComplessivo = 0;

        for (Investimenti investimento : investimenti) {
            int quantita = investimento.getQuantita();
            double prezzoAcquisto = investimento.getPrezzoAcquisto();
            double valoreAcquistoComplessivo = getValoreComplessivo(quantita, prezzoAcquisto);
            valoreComplessivo += valoreAcquistoComplessivo;

            LocalDate today = LocalDate.now();
            ValoriAzioni valoreAzione = valoriAzioniRepository.findByIdInvestimentoAndDataValore(investimento.getIdInvestimento(), today);

            if (valoreAzione != null) {
                double valoreAttuale = valoreAzione.getValore();
                double valoreAttualeInvestimento = valoreAttuale * quantita;
                valoreAttualeComplessivo += valoreAttualeInvestimento;
            }
        }

        return (int) ((valoreAttualeComplessivo - valoreComplessivo) / valoreComplessivo * 100);
    }

    /**
     * Calcola il valore complessivo di un investimento.
     *
     * @param quantita        La quantità di azioni.
     * @param prezzoAcquisto  Il prezzo di acquisto per azione.
     * @return Il valore complessivo dell'investimento.
     */
    private double getValoreComplessivo (int quantita, double prezzoAcquisto) {
        return quantita * prezzoAcquisto;
    }
}
