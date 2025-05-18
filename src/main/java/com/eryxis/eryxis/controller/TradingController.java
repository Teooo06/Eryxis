package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Azioni;
import com.eryxis.eryxis.model.Investimenti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.CarteRepository;
import com.eryxis.eryxis.repository.TransazioniRepository;
import com.eryxis.eryxis.service.*;
import com.eryxis.eryxis.service.externalAPI.AzioniService;
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
public class TradingController {
    @Autowired
    private UtentiService utentiService;
    @Autowired
    private AzioniService azioniService;
    @Autowired
    private InvestimentiService investimentiService;

    @GetMapping("/trading")
    public String trading(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            Utenti utente = utentiService.findByIdUtente(id);

            List<Investimenti> investimenti = investimentiService.findByUtente(utente);
            List<Azioni> azioni = azioniService.getListAzioni(0, 50);

            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("listaAzioni", azioni);

            if (!investimenti.isEmpty()){
                model.addAttribute("investimenti", investimenti);
            }
            else{
                model.addAttribute("investimenti", 1);
            }

            return "trading";
        }

        return "redirect:/login";
    }

    @PostMapping("/dettaglio-azione")
    public String dettaglioAzione(Model model,
                                  @RequestParam("symbol") String symbol,
                                  @RequestParam("exchangeShortName") String exchangeShortName,
                                  @RequestParam("name") String name,
                                  @RequestParam("price") float price) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            // Ottieni ulteriori dettagli dell'azione se necessario
            // Azioni azioneDettaglio = azioniService.getDettaglioAzione(symbol);

            // Aggiungi i dati al modello
            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("symbol", symbol);
            model.addAttribute("exchangeShortName", exchangeShortName);
            model.addAttribute("name", name);
            model.addAttribute("price", price);

            return "dettaglio-azione";
        }

        return "redirect:/login";
    }

    @PostMapping("/my-stock")
    public String myStock(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();
            Utenti utente = utentiService.findByIdUtente(id);

            List<Investimenti> investimenti = investimentiService.findByUtente(utente);

            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("investimenti", investimenti);
        }


        return "redirect:/login";
    }
}
