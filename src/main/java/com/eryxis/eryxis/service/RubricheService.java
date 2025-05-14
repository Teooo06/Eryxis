package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Rubriche;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.RubricheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RubricheService {
   @Autowired
    private RubricheRepository rubricheRepository;

   /**
    * Recupera un contatto dalla rubrica in base all'ID specificato.
    *
    * @param idContatto L'ID del contatto da cercare.
    * @return Il contatto corrispondente, se trovato.
    */
   public Rubriche findByIdContatto(int idContatto) {
       return rubricheRepository.findByIdContatto(idContatto);
   }

   /**
    * Restituisce tutti i contatti della rubrica associati a un determinato utente.
    *
    * @param utente L'utente per cui recuperare i contatti.
    * @return Lista di contatti collegati all'utente.
    */
   public List<Rubriche> findByUtente(Utenti utente) {
       return rubricheRepository.findByUtente(utente);
   }


    // funzioni per rimuovere o modificare
    /**
     * Salva un oggetto rubrica nel repository.
     *
     * @param rubrica Il contatto da salvare.
     */
    public void save(Rubriche rubrica) {
        rubricheRepository.save(rubrica);
    }

    /**
     * Salva un oggetto rubrica e lo restituisce.
     *
     * @param rubrica Il contatto da salvare.
     * @return Il contatto salvato.
     */
    public Rubriche getSave(Rubriche rubrica) {
        return rubricheRepository.save(rubrica);
    }

    /**
     * Elimina un contatto dalla rubrica in base al suo ID.
     *
     * @param id L'ID del contatto da eliminare.
     */
    public void deleteById(int id) {
        rubricheRepository.deleteById(id);
    }

    /**
     * Elimina un contatto specifico dalla rubrica.
     *
     * @param rubrica Il contatto da eliminare.
     */
    public void deleteByRubrica(Rubriche rubrica) {
        rubricheRepository.delete(rubrica);
    }

}
