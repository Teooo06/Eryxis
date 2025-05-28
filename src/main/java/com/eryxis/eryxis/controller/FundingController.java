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
            if (finanziamento.stream().anyMatch(f -> !f.getTipo().equals("verifica1")) || finanziamento.stream().anyMatch(f -> !f.getTipo().equals("verifica2"))) {
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

    @PostMapping("/finanziamenti")
    public String financing(Model model, @RequestParam("tipo") String tipo,
                                        @RequestParam("importo") int importo,
                                        @RequestParam("descrizione") String descrizione) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();
            Utenti utente = utenteService.findByIdUtente(userId);

            Finanziamenti finanziamento = new Finanziamenti();
            finanziamento.setImporto(importo);
            // Utilizziamo java.sql.Date che mantiene solo la parte della data
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            finanziamento.setDataErogazione(currentDate);
            finanziamento.setInteressi(0);
            finanziamento.setSpesaIncasso(2.5);
            finanziamento.setTipoRata("mensile");
            finanziamento.setInizioPagamento(currentDate);
            finanziamento.setImportoPagato(0);
            finanziamento.setDescrizione(descrizione);
            finanziamento.setUtente(utente);
            finanziamento.setStato(false);
            if (tipo.equals("mutuo")) {
                finanziamento.setTipo("verifica1");
            } else if (tipo.equals("prestito")) {
                finanziamento.setTipo("verifica2");
            }
            finanziamento.setValoreRata(0);

            finanziamentoService.save(finanziamento);
            return "redirect:/funding";
        }

        return "redirect:/login";
    }

    @GetMapping("/financials")
    public String financials(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof CustomAuthenticationToken customAuth) {
            int userId = customAuth.getIdUtente();
            Utenti utente = utenteService.findByIdUtente(userId);
            List<Finanziamenti> finanziamento = finanziamentoService.findByUtente(utente);

            model.addAttribute("finanziamenti", finanziamento);
            model.addAttribute("nome", utente.getNome());
            model.addAttribute("cognome", utente.getCognome());

            return "applied-funding";
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

                    double rata = getRata(type, finanziamento);

                    finanziamento.setValoreRata(rata);

                    // Utilizziamo java.sql.Date che mantiene solo la parte della data
                    java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                    finanziamento.setDataErogazione(currentDate);
                    finanziamento.setInizioPagamento(currentDate);
                    finanziamento.setStato(true);
                    finanziamentoService.save(finanziamento);

                    return "redirect:/funding";
                }
            }
            else{
                return "redirect:/funding";
            }
        }
        return "redirect:/login";
    }

    private static double getRata(String type, Finanziamenti finanziamento) {
        double rate = finanziamento.getInteressi() / 100;
        double i = 0;
        int n = 5;

        switch (type) {
            case "mensile" -> {
                i = rate / 12;
                n = 5 * 12;
            }
            case "bimestrale" -> {
                i = rate / 6;
                n = 5 * 6;
            }
            case "trimestrale" -> {
                i = rate / 4;
                n = 5 * 4;
            }
            case "semestrale" -> {
                i = rate / 2;
                n = 5 * 2;
            }
            case "annuale" -> {
                i = rate;
                n = 5;
            }
            default -> {
                i = rate / 12;
                n = 5 * 12;
            }
        }

        double potenza = Math.pow(1 + i, n);
        double rata = finanziamento.getImporto() * (i * potenza) / (potenza - 1);

        return rata;
    }

}
