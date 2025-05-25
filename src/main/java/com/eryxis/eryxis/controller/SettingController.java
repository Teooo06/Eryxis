package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Transazioni;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.CarteService;
import com.eryxis.eryxis.service.ContiService;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SettingController {

    @Autowired
    private final CarteService carteService;
    @Autowired
    private ContiService contiService;
    @Autowired
    private UtentiService utentiService;

    private static final Map<String, String> CURRENCY_SYMBOLS = Map.ofEntries(
            Map.entry("EUR", "€"),
            Map.entry("USD", "$"),
            Map.entry("GBP", "£"),
            Map.entry("JPY", "¥"),
            Map.entry("CHF", "Fr"),     // Franco svizzero
            Map.entry("CAD", "$"),      // Dollaro canadese
            Map.entry("AUD", "$"),      // Dollaro australiano
            Map.entry("CNY", "¥"),      // Yuan cinese
            Map.entry("RUB", "₽"),      // Rublo russo
            Map.entry("KRW", "₩")      // Won sudcoreano
    );

    @GetMapping("/setting")
    public String setting(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            List<Carte> carte = customAuth.getCarte();
            List<String> ordineTipo = Arrays.asList("credito", "debito", "prepagata");
            int idUser = customAuth.getIdUtente();
            Utenti utente = utentiService.findByIdUtente(idUser);

            Conti conto = contiService.findByUtente(utente);


            if (carte != null && carte.size() > 0) {
                carte.sort(Comparator.comparing(carta -> ordineTipo.indexOf(carta.getTipo())));
            }

            if (conto != null) {
                model.addAttribute("id", id);
                model.addAttribute("nome", nome);
                model.addAttribute("cognome", cognome);
                model.addAttribute("carte", carte);
                model.addAttribute("cardCount", carte.size());
                model.addAttribute("valuta", CURRENCY_SYMBOLS.getOrDefault(conto.getValuta(), conto.getValuta()));

                return  "setting";
            }
        }

        return "redirect:/login";
    }

    @PostMapping("/modificaCarta")
    public void modificaCarta(@RequestParam String numeroCarta,
                                @RequestParam boolean stato) {
        carteService.aggiornaCarta(numeroCarta, stato);
    }
}
