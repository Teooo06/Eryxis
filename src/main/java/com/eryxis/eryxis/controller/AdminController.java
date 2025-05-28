package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Finanziamenti;
import com.eryxis.eryxis.model.Tickets;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.FinanziamentiService;
import com.eryxis.eryxis.service.UtentiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private FinanziamentiService finanziamentiService;
    @Autowired
    private UtentiService utentiService;

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

    @PostMapping("/cambiaTasso")
    public String risolviTicket(@RequestParam int idFinanziamento,
                                @RequestParam double tasso) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated with CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();
            Utenti utente = utentiService.findByIdUtente(userId);
            String codicePermesso = utente.getPermesso().getCodicePermesso();

            if (!"07".equals(codicePermesso) && !"05".equals(codicePermesso)) {
                return "redirect:/login"; // Redirect if the user is not authorized
            }

            finanziamentiService.aggiornaTasso(idFinanziamento, tasso);

            return "redirect:/creditManagement"; // Redirect to the admin support page
        }

        return "redirect:/login"; // Redirect if the user is not authenticated
    }


}
