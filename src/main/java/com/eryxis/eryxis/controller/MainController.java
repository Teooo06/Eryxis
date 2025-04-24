package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Transazioni;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.CarteService;
import com.eryxis.eryxis.service.ContiService;
import com.eryxis.eryxis.service.TransazioniService;
import com.eryxis.eryxis.service.UtentiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {
    @Autowired
    private CarteService carteService;
    @Autowired
    private ContiService contiService;
    @Autowired
    private UtentiService utentiService;
    @Autowired
    private TransazioniService transazioniService;

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

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false)  String msg) {
        model.addAttribute("msg", msg);
        return "login";
    }


    // Homepage dopo la verifica dell'OTP
    @GetMapping("/home")
    public String home(Model model, @RequestParam int id, @RequestParam String nome, @RequestParam String cognome, HttpSession session) {

        if (id == 0) {
            return "home"; // Errore
        }
        Utenti utente = utentiService.findByIdUtente(id);
        if( utente != null ) {
            List<Conti> conto = contiService.findByUtente(utente);
            if (!conto.isEmpty()) {
                List<Carte> carte = carteService.findByConto(conto.get(0));
                List<Transazioni> transazioni = transazioniService.findByConto(conto.get(0));
                model.addAttribute("nome", nome);
                model.addAttribute("cognome", cognome);
                model.addAttribute("carte", carte);
                model.addAttribute("cardCount", carte.size());
                model.addAttribute("transazioni", transazioni);
                model.addAttribute("valuta", CURRENCY_SYMBOLS.getOrDefault(conto.get(0).getValuta(), conto.get(0).getValuta()));
                model.addAttribute("saldo", conto.get(0).getSaldo());

                return "index";
            }
        }

        return "home"; // Ritorna la vista home.html o home.jsp
    }

    @GetMapping("/setting")
    public String setting(Model model) {
        return "setting";
    }

    @GetMapping("/trading")
    public String trading(Model model) {
        return "trading";
    }
}
