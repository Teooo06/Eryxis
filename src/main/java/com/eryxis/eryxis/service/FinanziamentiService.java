package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Finanziamenti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.FinanziamentiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanziamentiService {
    @Autowired
    private FinanziamentiRepository finanziamentiRepository;

    /**
     * Trova un finanziamento in base all'ID.
     *
     * @param idFinanziamento L'ID del finanziamento da trovare.
     * @return Il finanziamento corrispondente all'ID specificato.
     */
    public Finanziamenti findByIdFinanziamento(int idFinanziamento) {
        return finanziamentiRepository.findByIdFinanziamento(idFinanziamento);
    }

    /**
     * Restituisce la lista di finanziamenti associati a un determinato utente.
     *
     * @param idUtente L'oggetto utente per cui recuperare i finanziamenti.
     * @return Lista di finanziamenti collegati all'utente.
     */
    public List<Finanziamenti> findByUtente(Utenti idUtente) {
        return finanziamentiRepository.findByUtente(idUtente);
    }

    /**
     * Restituisce tutti i finanziamenti presenti nel repository.
     *
     * @return Lista di tutti i finanziamenti.
     */
    public List<Finanziamenti> findAll(){return finanziamentiRepository.findAll();}

    /**
     * Aggiorna il tasso di interesse di un finanziamento specifico.
     *
     * @param finanziamento Finanziamento da aggiornare.
     * @param interessi Il nuovo tasso di interesse da impostare.
     */
    public void aggiornaTasso(Finanziamenti finanziamento, double interessi) {

        if (finanziamento != null) {
            finanziamento.setInteressi(interessi);
            save(finanziamento);
        }
    }


    // funzioni di base per aggiungere o rimuovere
    /**
     * Salva un finanziamento nel repository.
     *
     * @param finanziamento Il finanziamento da salvare.
     */
    public void save(Finanziamenti finanziamento) {
        finanziamentiRepository.save(finanziamento);
    }

    /**
     * Salva un finanziamento e lo restituisce.
     *
     * @param finanziamento Il finanziamento da salvare.
     * @return Il finanziamento salvato.
     */
    public Finanziamenti getSave(Finanziamenti finanziamento) {
        return finanziamentiRepository.save(finanziamento);
    }

    /**
     * Elimina un finanziamento in base all'ID.
     *
     * @param idFinanziamento L'ID del finanziamento da eliminare.
     */
    public void deleteById(int idFinanziamento) {
        finanziamentiRepository.deleteById(idFinanziamento);
    }

    /**
     * Elimina un finanziamento specifico.
     *
     * @param finanziamento Il finanziamento da eliminare.
     */
    public void deleteByFinanziamento(Finanziamenti finanziamento) {
        finanziamentiRepository.delete(finanziamento);
    }

    public List<Finanziamenti> getAll(){return finanziamentiRepository.findAll();}

}
