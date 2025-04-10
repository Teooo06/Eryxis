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

    public Logs findByIdLog(int idLog) {
        return logsRepository.findByIdLog(idLog);
    }

    public List<Logs> findByUtente(Utenti utente) {
        return logsRepository.findByUtente(utente);
    }

    // funzioni per aggiornamento e rimozione
    public void save(Logs logs) {
        logsRepository.save(logs);
    }
    public Logs getSage(Logs logs) {
        return logsRepository.save(logs);
    }
    public void deleteByLogs(Logs logs) {
        logsRepository.delete(logs);
    }
    public void deleteById(int idLog) {
        logsRepository.deleteById(idLog);
    }
}
