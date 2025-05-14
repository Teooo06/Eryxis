package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Logs;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogsService {
    @Autowired
    private LogsRepository logsRepository;

    /**
     * Recupera un log specifico in base al suo ID.
     *
     * @param idLog L'ID del log da cercare.
     * @return Il log corrispondente, se trovato.
     */
    public Logs findByIdLog(int idLog) {
        return logsRepository.findByIdLog(idLog);
    }

    /**
     * Restituisce tutti i log associati a un determinato utente.
     *
     * @param utente L'utente per cui recuperare i log.
     * @return Lista di log collegati all'utente.
     */
    public List<Logs> findByUtente(Utenti utente) {
        return logsRepository.findByUtente(utente);
    }

    /**
     * Salva un oggetto log nel repository.
     *
     * @param logs Il log da salvare.
     */
    public void save(Logs logs) {
        logsRepository.save(logs);
    }

    /**
     * Salva un oggetto log e lo restituisce.
     *
     * @param logs Il log da salvare.
     * @return Il log salvato.
     */
    public Logs getSage(Logs logs) {
        return logsRepository.save(logs);
    }

    /**
     * Elimina un log specifico dal repository.
     *
     * @param logs Il log da eliminare.
     */
    public void deleteByLogs(Logs logs) {
        logsRepository.delete(logs);
    }

    /**
     * Elimina un log in base al suo ID.
     *
     * @param idLog L'ID del log da eliminare.
     */
    public void deleteById(int idLog) {
        logsRepository.deleteById(idLog);
    }
}
