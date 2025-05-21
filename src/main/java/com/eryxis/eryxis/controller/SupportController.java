package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Tickets;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.TicketsService;
import com.eryxis.eryxis.service.UtentiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SupportController {
    @Autowired
    private TicketsService ticketsService;
    @Autowired
    private UtentiService utentiService;

    @GetMapping("/support")
    public String support(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated with CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);

            Utenti utente = utentiService.findByIdUtente(userId);
            List<Tickets> tickets = ticketsService.findByUtente(utente);

            model.addAttribute("tickets", tickets);

            return "support";
        }

        return "redirect:/login";
    }

    @PostMapping("/support")
    public String createTicket(Model model,
                               @RequestParam String titolo,
                               @RequestParam String descrizione) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated with CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();

            Utenti utente = utentiService.findByIdUtente(userId);
            Tickets ticket = new Tickets();
            ticket.setTitolo(titolo);
            ticket.setDescrizione(descrizione);
            ticket.setStato((short) 0);
            ticket.setUtente(utente);
            ticketsService.save(ticket);

            List<Tickets> tickets = ticketsService.findByUtente(utente);

            model.addAttribute("tickets", tickets);

            return "support";
        }

        return "redirect:/login";
    }

    @GetMapping("/supportAdmin")
    public String getAllTicket(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated with CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();
            Utenti utente = utentiService.findByIdUtente(userId);
            String codicePermesso = utente.getPermesso().getCodicePermesso();

            if (!"07".equals(codicePermesso) && !"05".equals(codicePermesso)) {
                return "redirect:/login"; // Redirect if the user is not authorized
            }
            List<Tickets> tickets = ticketsService.findAll();

            model.addAttribute("tickets", tickets);
            model.addAttribute("nome", customAuth.getNome());

            // pagina admin per tickets
            // return "supportAdmin";
        }

        return "redirect:/login"; // Redirect if the user is not authenticated
    }
}
