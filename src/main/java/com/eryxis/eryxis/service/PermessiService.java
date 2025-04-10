package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Permessi;
import com.eryxis.eryxis.repository.PermessiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermessiService {
    @Autowired
    private PermessiRepository permessiRepository;

    public Permessi findByPermesso(int idPermesso) {
        return permessiRepository.findByIdPermesso(idPermesso);
    }


    // funzioni per salvare o rimuovere
    public void save(Permessi permesso) {
        permessiRepository.save(permesso);
    }

    public Permessi getSave(Permessi permesso) {
        return permessiRepository.save(permesso);
    }

    public void deleteByPermesso(Permessi permesso) {
        permessiRepository.delete(permesso);
    }

    public void deleteById(int id) {
        permessiRepository.deleteById(id);
    }

}
