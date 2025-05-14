package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Permessi;
import com.eryxis.eryxis.repository.PermessiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermessiService {
    @Autowired
    private PermessiRepository permessiRepository;

    /**
     * Recupera un permesso in base all'ID specificato.
     *
     * @param idPermesso L'ID del permesso da cercare.
     * @return Il permesso corrispondente, se trovato.
     */
    public Permessi findByPermesso(int idPermesso) {
        return permessiRepository.findByIdPermesso(idPermesso);
    }


    // funzioni per salvare o rimuovere
    /**
     * Salva un oggetto permesso nel repository.
     *
     * @param permesso Il permesso da salvare.
     */
    public void save(Permessi permesso) {
        permessiRepository.save(permesso);
    }

    /**
     * Salva un oggetto permesso e lo restituisce.
     *
     * @param permesso Il permesso da salvare.
     * @return Il permesso salvato.
     */
    public Permessi getSave(Permessi permesso) {
        return permessiRepository.save(permesso);
    }

    /**
     * Elimina un permesso specifico dal repository.
     *
     * @param permesso Il permesso da eliminare.
     */
    public void deleteByPermesso(Permessi permesso) {
        permessiRepository.delete(permesso);
    }

    /**
     * Elimina un permesso in base all'ID specificato.
     *
     * @param id L'ID del permesso da eliminare.
     */
    public void deleteById(int id) {
        permessiRepository.deleteById(id);
    }

}
