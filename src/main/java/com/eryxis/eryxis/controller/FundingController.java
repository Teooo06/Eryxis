package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Finanziamenti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.FinanziamentiService;
import com.eryxis.eryxis.service.UtentiService;
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
public class FundingController {
    @Autowired
    private FinanziamentiService finanziamentoService;
    @Autowired
    private UtentiService utenteService;

    @GetMapping("/funding")
    public String funding(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();
            Utenti utente = utenteService.findByIdUtente(userId);
            List<Finanziamenti> finanziamento = finanziamentoService.findByUtente(utente);
            int listSize = 0;

            model.addAttribute("finanziamenti", finanziamento);
            model.addAttribute("nome", utente.getNome());
            model.addAttribute("cognome", utente.getCognome());
            if (finanziamento.stream().anyMatch(f -> !f.getTipo().equals("verifica"))) {
                listSize = 100;
            }
            else if (finanziamento.stream().anyMatch(f -> !f.getTipo().equals("mutuo") && !f.getTipo().equals("prestito"))) {
                listSize++;
            }

            model.addAttribute("fundingCount", listSize);

            return "funding";
        }

        return "redirect:/login";
    }

    @PostMapping("/apply-financials")
    public String applyFinancials(Model model, @RequestParam("action") String action,
                                                @RequestParam("idFinanziamento") int id,
                                                @RequestParam("tipo-rata") String type) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();
            Utenti utente = utenteService.findByIdUtente(userId);
            Finanziamenti finanziamento = finanziamentoService.findByIdFinanziamento(id);

            if (finanziamento != null) {
                if (action.equals("decline")){
                    finanziamentoService.deleteByFinanziamento(finanziamento);
                    return "redirect:/funding";
                }
                else if (action.equals("accept")){
                    if (finanziamento.getTipo().equals("verifica1")) {
                        finanziamento.setTipo("mutuo");
                    }
                    else if (finanziamento.getTipo().equals("verifica2")) {
                        finanziamento.setTipo("prestito");
                    }

                    finanziamento.setTipoRata(type);
                }
            }
            else{
                return "redirect:/funding";
            }
        }
        return "redirect:/login";
    }
}
