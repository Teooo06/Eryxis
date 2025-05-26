package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Azioni;
import com.eryxis.eryxis.model.Histories;
import com.eryxis.eryxis.service.Security.OTPService;
import com.eryxis.eryxis.service.Security.PasswordService;
import com.eryxis.eryxis.service.externalAPI.AzioniService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quote")
public class AzioniController {
    @Autowired
    private AzioniService azioniService;

    @Autowired
    private PasswordService passwordService;

    // funzione GET per richiedere la lista delle azioni
    @GetMapping("/azioni")
    public List<Azioni> getAzioni(
            @RequestParam int posInizio,
            @RequestParam int posFine) {
        return azioniService.getListAzioni(posInizio, posFine);
    }

    @GetMapping("/azione")
    public Histories getDatiAzione(
            @RequestParam String symbol) {
        return azioniService.getDatiAzione(symbol);
    }

    @GetMapping("/OTPGENERATE")
    public String otpGenra(){
        String otpSecret = OTPService.generateSecretKey();
        try {
            return passwordService.encrypt(otpSecret);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la cifratura: " + e.getMessage());
        }
    }

    /* End point per hashare password (se mai dovesse servire)
    @GetMapping("/hash")
    public String getHash(@RequestParam String password) {
        return passwordService.hashPassword(password);
    }
     */

    /*
    @GetMapping("/encrypt")
    public String getEncrypt(@RequestParam String password) {
        try {
            return passwordService.encrypt(password);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la cifratura: " + e.getMessage());
        }
    }
     */

    @GetMapping("/encrypt")
    public String getEncrypt(@RequestParam String password) {
        try {
            return passwordService.encrypt(password);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la cifratura: " + e.getMessage());
        }
    }
}
