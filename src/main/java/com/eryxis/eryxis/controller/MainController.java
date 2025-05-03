package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Transazioni;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.CarteRepository;
import com.eryxis.eryxis.repository.TransazioniRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private CarteController carteController;

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
    @Autowired
    private CarteRepository carteRepository;
    @Autowired
    private TransazioniRepository transazioniRepository;

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

            List<String> ordineTipo = Arrays.asList("credito", "debito", "prepagata");

            carte.sort(Comparator.comparing(carta -> ordineTipo.indexOf(carta.getTipo())));

            Utenti utente = utentiService.findByIdUtente(id);
            if (utente != null) {
                List<Conti> conto = contiService.findByUtente(utente);
                if (!conto.isEmpty()) {
                    List<Transazioni> transazioni = transazioniService.findByConto(conto.get(0));
                    boolean hasDebito = carte.stream().anyMatch(c -> c.getTipo().equals("debito"));
                    boolean hasPrepagata = carte.stream().anyMatch(c -> c.getTipo().equals("prepagata"));

                    transazioni.sort(Comparator.comparing(Transazioni::getDataTransazione).reversed());

                    model.addAttribute("id", id);
                    model.addAttribute("nome", nome);
                    model.addAttribute("cognome", cognome);
                    model.addAttribute("carte", carte);
                    model.addAttribute("cardCount", carte.size());
                    model.addAttribute("transazioni", transazioni);
                    model.addAttribute("valuta", CURRENCY_SYMBOLS.getOrDefault(conto.get(0).getValuta(), conto.get(0).getValuta()));
                    model.addAttribute("saldo", conto.get(0).getSaldo());
                    model.addAttribute("hasDebito", hasDebito);
                    model.addAttribute("hasPrepagata", hasPrepagata);

                    return "index"; // questa deve essere la tua index.html in templates/
                }
            }
        }

        // Se l'autenticazione non è valida, reindirizza alla pagina di login
        return "redirect:/login";
    }


    @GetMapping("/setting")
    public String setting(Model model) {
        return "setting";
    }

    @GetMapping("/trading")
    public String trading(Model model) {
        return "trading";
    }

    @PostMapping("/addDebit")
    public String addDebit(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            List<Carte> carte = customAuth.getCarte();
            Conti conto = carte.get(0).getConto();
            if (conto != null){
                Utenti utente = conto.getUtente();
                Carte carta = carteController.cartaNuova(conto, "debito");

                if (carta != null) {
                    carteRepository.save(carta);
                }
                carte.add(carta);

                Transazioni transazioni = new Transazioni();
                transazioni.setConto(conto);
                transazioni.setImporto(-14.99);
                transazioni.setTipo("bonifico");
                transazioni.setDestinatario("Eryxis Bank S.P.A.");
                transazioni.setCausale("acquisto");

                transazioniRepository.save(transazioni);

                conto.setSaldo(conto.getSaldo() - transazioni.getImporto());

                return "redirect:/home";
            }
        }
        else{
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @PostMapping("/addPrepaid")
    public String addPrepaid(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            List<Carte> carte = customAuth.getCarte();
            Conti conto = carte.get(0).getConto();
            if (conto != null){
                Utenti utente = conto.getUtente();
                Carte carta = carteController.cartaNuova(conto, "prepagata");

                if (carta != null) {
                    carteRepository.save(carta);
                }
                carte.add(carta);

                Transazioni transazioni = new Transazioni();
                transazioni.setConto(conto);
                transazioni.setImporto(-4.99);
                transazioni.setTipo("bonifico");
                transazioni.setDestinatario("Eryxis Bank S.P.A.");
                transazioni.setCausale("acquisto");

                transazioniRepository.save(transazioni);

                conto.setSaldo(conto.getSaldo() - transazioni.getImporto());

                return "redirect:/home";
            }
        }
        else{
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @PostMapping("/submit-transaction")
    public String submitTransaction(Model model, @RequestParam("importo") float importo, @RequestParam("tipo") String tipo,
                                    @RequestParam("iban") String iban, @RequestParam("destinatario") String destinatario,
                                    @RequestParam("causale") String causale, @RequestParam("carte") String carta) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken customAuth) {
            List<Carte> carte = customAuth.getCarte();

            if (carte != null) {
                if (carte.stream().anyMatch(c -> c.getTipo().equals(carta))) {
                    Carte card = carte.stream().filter(c -> c.getTipo().equals(carta)).findFirst().get();

                    card.setSaldoDisponibile(card.getSaldoDisponibile() - importo);
                    carteRepository.save(card);

                    Transazioni transazioni = new Transazioni();
                    transazioni.setConto(card.getConto());
                    transazioni.setImporto(-importo);
                    transazioni.setTipo(tipo);
                    transazioni.setDestinatario(destinatario);
                    transazioni.setCausale(causale);
                    transazioniRepository.save(transazioni);

                    Conti conto = contiService.findByIBAN(iban);
                    if (conto != null) {
                        conto.setSaldo(conto.getSaldo() + importo);
                        contiService.save(conto);
                    }
                } else {
                    alert("X-ALERT-MESSAGE", "Carta non trovata! Riprova più tardi.");
                }
            } else {
                alert("X-ALERT-MESSAGE", "Sessione scaduta! Eseguire nuovamente l'accesso.");
            }
        }
        else{
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @PostMapping("/alert")
    public ResponseEntity<String> alert(String header, String message) {
        return ResponseEntity.ok()
                .header(header, message)
                .body("OK");
    }
}