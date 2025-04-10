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

   public Rubriche findByIdContatto(int idContatto) {
       return rubricheRepository.findByIdContatto(idContatto);
   }

   public List<Rubriche> findByUtente(Utenti utente) {
       return rubricheRepository.findByUtente(utente);
   }


    // funzioni per rimuovere o modificare
    public void save(Rubriche rubrica) {
        rubricheRepository.save(rubrica);
    }

    public Rubriche getSave(Rubriche rubrica) {
        return rubricheRepository.save(rubrica);
    }

    public void deleteById(int id) {
        rubricheRepository.deleteById(id);
    }

    public void deleteByRubrica(Rubriche rubrica) {
        rubricheRepository.delete(rubrica);
    }

}
