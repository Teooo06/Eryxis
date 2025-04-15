package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.UtentiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
@Controller
@RequiredArgsConstructor
public class RegisterController {

    @Autowired
    private UtentiService utentiService;

    @GetMapping("/register")
    public String registerPage(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("msg", msg);
        return "register";
    }

    /* Ci sono 3 pagine,
        nella prima (register) viene inserito nome, cognome, mail
        nella seconda(registerInfo) viene inserito datadinascita, indirizzo, codice fiscale, prefisso e telefono
        nella terza (registerPassword) viene inserita la password e la conferma della password
        nella quarta (registerOTP) viene chiesta la scelta per utilizzare l'otp di google o l'otp via mail
    */

    @PostMapping("/registerInfo")
    public String registerInfo(@ModelAttribute("utenti") Utenti utenti, Model model) {
        //TODO
        return null;
    }

    @PostMapping("/registerPassword")
    public String registerPassword(@ModelAttribute("utenti") Utenti utenti, Model model) {
        //TODO
        return null;
    }

    @PostMapping("/registerOTP")
    public String registerOTP(@ModelAttribute("utenti") Utenti utenti, Model model) {
        //TODO
        return null;
    }

    @PostMapping("/registerUser")
    public void register(@RequestParam String nome,
                         @RequestParam String cognome,
                         @RequestParam String mail,
                         @RequestParam Date dataNascita,
                         @RequestParam String indirizzo,
                         @RequestParam String codiceFiscale,
                         @RequestParam String prefisso,
                         @RequestParam String telefono,
                         @RequestParam String password,
                         @RequestParam boolean OTP) {

        if (utentiService.esisteByMail(mail)) {
            // esiste già una mail registrata
        }

        // capire se va convertita in qualche modo la data di nascita
        Utenti utente = new Utenti(nome, cognome, (java.sql.Date) dataNascita, indirizzo, codiceFiscale, mail, prefisso, telefono, password, OTP);
        utentiService.save(utente);


    }
}
