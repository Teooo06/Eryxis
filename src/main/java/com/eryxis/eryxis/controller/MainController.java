package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.*;
import com.eryxis.eryxis.repository.CarteRepository;
import com.eryxis.eryxis.repository.TransazioniRepository;
import com.eryxis.eryxis.service.*;
import com.eryxis.eryxis.service.externalAPI.AzioniService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {
    @Autowired
    private ContiService contiService;
    @Autowired
    private UtentiService utentiService;
    @Autowired
    private TransazioniService transazioniService;
    @Autowired
    private RubricheService rubricheService;

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
    public String home(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();
            List<Carte> carte = customAuth.getCarte();

            Utenti utente = utentiService.findByIdUtente(id);

            List<String> ordineTipo = Arrays.asList("credito", "debito", "prepagata");

            List<Rubriche> rubriche = rubricheService.findByUtente(utente);

            if (carte != null && carte.size() > 0) {
                carte.sort(Comparator.comparing(carta -> ordineTipo.indexOf(carta.getTipo())));
            }

            if (utente != null) {
                if (utente.getPermesso().getIdPermesso() == 1){
                    List<Conti> conti = contiService.findAll();
                    List<Utenti> utenti = utentiService.findByIdPermesso(2);

                    model.addAttribute("nome", nome);
                    model.addAttribute("cognome", cognome);
                    model.addAttribute("conti", conti);

                    return  "adPage";
                }
                Conti conto = contiService.findByUtente(utente);
                if (conto != null) {
                    List<Transazioni> transazioni = transazioniService.findByConto(conto);
                    boolean hasDebito = carte.stream().anyMatch(c -> c.getTipo().equals("debito"));
                    boolean hasPrepagata = carte.stream().anyMatch(c -> c.getTipo().equals("prepagata"));

                    transazioni.sort(
                            Comparator.comparing(Transazioni::getDataTransazione, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                    );
                    model.addAttribute("id", id);
                    model.addAttribute("nome", nome);
                    model.addAttribute("cognome", cognome);
                    model.addAttribute("carte", carte);
                    model.addAttribute("cardCount", carte.size());
                    model.addAttribute("transazioni", transazioni);
                    model.addAttribute("valuta", CURRENCY_SYMBOLS.getOrDefault(conto.getValuta(), conto.getValuta()));
                    model.addAttribute("saldo", conto.getSaldo());
                    model.addAttribute("hasDebito", hasDebito);
                    model.addAttribute("hasPrepagata", hasPrepagata);
                    model.addAttribute("rubriche", rubriche);

                    return "index"; // questa deve essere la tua index.html in templates/
                }
            }
        }

        // Se l'autenticazione non è valida, reindirizza alla pagina di login
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SecurityContextHolder.clearContext(); // pulisce il contesto di sicurezza
        }
        return "redirect:/"; // reindirizza alla pagina di login
    }
}