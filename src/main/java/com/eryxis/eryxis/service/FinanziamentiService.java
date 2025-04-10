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

    private Finanziamenti findByIdFinanziamento(int idFinanziamento) {
        return finanziamentiRepository.findByIdFinanziamento(idFinanziamento);
    }

    public List<Finanziamenti> findByUtente(Utenti idUtente) {
        return finanziamentiRepository.findByUtente(idUtente);
    }


    // funzioni di base per aggiungere o rimuovere
    public void save(Finanziamenti finanziamento) {
        finanziamentiRepository.save(finanziamento);
    }

    public Finanziamenti getSave(Finanziamenti finanziamento) {
        return finanziamentiRepository.save(finanziamento);
    }

    public void deleteById(int idFinanziamento) {
        finanziamentiRepository.deleteById(idFinanziamento);
    }

    public void deleteByFinanziamento(Finanziamenti finanziamento) {
        finanziamentiRepository.delete(finanziamento);
    }
}
