package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Permessi;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.UtentiRepository;
import com.eryxis.eryxis.service.Security.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtentiService {
    @Autowired
    private UtentiRepository utentiRepository;
    @Autowired
    private PermessiService permessiService;
    @Autowired
    private PasswordService passwordService;

    /**
     * Recupera un utente a partire dall'ID specificato.
     *
     * @param idUtente L'ID dell'utente da cercare.
     * @return L'utente corrispondente, se trovato.
     */
    public Utenti findByIdUtente(int idUtente) {
        return utentiRepository.findByIdUtente(idUtente);
    }

    /**
     * Recupera un utente a partire dall'indirizzo email specificato.
     *
     * @param mail L'indirizzo email dell'utente da cercare.
     * @return L'utente corrispondente, se trovato.
     */
    public Utenti findByMail(String mail) {
        return utentiRepository.findByMail(mail);
    }

    /**
     * Verifica se un utente esiste già con l'indirizzo email specificato.
     *
     * @param mail L'indirizzo email da verificare.
     * @return true se l'utente esiste, false altrimenti.
     */
    public boolean esisteByMail(String mail) {
        return utentiRepository.existsByMail(mail);
    }

    /**
     * Recupera un utente a partire dall'indirizzo email e dalla password.
     *
     * @param mail L'indirizzo email dell'utente.
     * @param password La password dell'utente.
     * @return L'utente corrispondente, se trovato.
     */
    public Utenti findByMailAndPassword(String mail, String password) {
        return utentiRepository.findByMailAndPassword(mail, password);
    }

    /**
     * Verifica se esiste un utente con l'indirizzo email e la password specificati.
     *
     * @param mail L'indirizzo email dell'utente.
     * @param password La password dell'utente.
     * @return true se l'utente esiste, false altrimenti.
     */
    public boolean findByMailAndPasswordBool(String mail, String password) {
        Utenti utente = utentiRepository.findByMail(mail);
        if (utente == null) return false;

        return passwordService.verifyPassword(password, utente.getPassword());
    }

    /**
     * Recupera la passphrase associata all'indirizzo email dell'utente.
     *
     * @param mail L'indirizzo email dell'utente.
     * @return La passphrase dell'utente, se trovata.
     */
    public String findPassPhrase(String mail) {
        Utenti utente = utentiRepository.findByMail(mail);
        if (utente != null) {
            String passphrase = utente.getPassPhrase();
            try {
                return passwordService.decrypt(passphrase);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Verifica se l'utente con l'indirizzo email specificato ha abilitato l'OTP.
     *
     * @param mail L'indirizzo email dell'utente.
     * @return true se l'OTP è abilitato, false altrimenti.
     */
    public boolean useOTP(String mail) {
        Utenti utente = utentiRepository.findByMail(mail);
        if (utente != null) {
            return utente.isOTP();
        }
        return true;
    }

    public List<Utenti> findByIdPermesso (int idPermesso) {
        Permessi permesso = permessiService.findByPermesso(idPermesso);

        return utentiRepository.findByPermesso(permesso);
    }

    // funzioni di base per aggiungere o rimuovere

    /**
     * Salva un utente nel repository.
     *
     * @param utente L'utente da salvare.
     */
    public void save(Utenti utente) {
        utentiRepository.save(utente);
    }

    /**
     * Elimina un utente in base al suo ID.
     *
     * @param idUtente L'ID dell'utente da eliminare.
     */
    public void deleteById(int idUtente) {
        utentiRepository.deleteById(idUtente);
    }

    /**
     * Elimina un utente specifico dal repository.
     *
     * @param utente L'utente da eliminare.
     */
    public void deleteByUtente(Utenti utente) {
        utentiRepository.delete(utente);
    }
}
