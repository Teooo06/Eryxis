package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.UtentiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private UtentiService utentiService;

    // Metodi HTTP GET

    // ritorna la pagina di login
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("msg", msg);
        return "login";
    }


    // Metodi HTTP POST

    // esegue la prima verifica di login
    @PostMapping("/login")
    public String login(Model model, @RequestParam String email, @RequestParam String password) {
        // TODO: aggiungere dei controlli per evitare delle query injection

        if (!utentiService.findByMailAndPasswordBool(email, password)) {
            model.addAttribute("msg", "Credenziali errate");
            return "login";
        }

        // Reindirizza alla pagina OTP
        return "redirect:/auth/otp?email=" + email;
    }
}
