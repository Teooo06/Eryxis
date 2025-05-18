package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Finanziamenti;
import com.eryxis.eryxis.service.FinanziamentiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private FinanziamentiService finanziamentiService;

    @GetMapping("/creditManagement")
    public String creditManagement(Model model) {
        List<Finanziamenti> finanziamenti = finanziamentiService.findAll();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);

            return "creditManagement";
        }

        return "redirect:/login";
    }
}
