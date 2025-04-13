package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.UtentiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("Authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private UtentiService utentiService;

    // Metodi HTTP GET
    @RequestMapping("/login")
    public String loginPage(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("msg", msg);
        return "login";
    }

    @RequestMapping("/register")
    public String registerPage(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("msg", msg);
        return "register";
    }

    // Metodi HTTP POST
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        // TODO: aggiungere dei controlli per evitare delle query injection

        if (!utentiService.findByMailAndPasswordBool(email, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.LOCATION, "/?msg=Credenziali errate")
                    .build();
        }

        boolean isValidUser = true;
        boolean otpValid = true;

        if (isValidUser) {
            // Reindirizza alla pagina OTP
            String redirectUrl = "/auth/otp?email=" + email;
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        }

        // Reindirizza alla pagina di login con messaggio di errore
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.LOCATION, "/?msg=Credenziali errate")
                .build();
    }

    @PostMapping("/register")
    public void register(@RequestParam String nome,
                         @RequestParam String cognome,
                         @RequestParam String mail,
                         @RequestParam Date dataNascita,
                         @RequestParam String indirizzo,
                         @RequestParam String codiceFiscale,
                         @RequestParam String prefisso,
                         @RequestParam String telefono,
                         @RequestParam String password) {

        if (utentiService.esisteByMail(mail)) {
            // esiste già una mail registrata
        }

        // capire se va convertita in qualche modo la data di nascita
        Utenti utente = new Utenti(nome, cognome, (java.sql.Date) dataNascita, indirizzo, codiceFiscale, mail, prefisso, telefono, password);

    }


}
