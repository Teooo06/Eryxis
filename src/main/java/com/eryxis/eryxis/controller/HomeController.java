package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Transazioni;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.CarteRepository;
import com.eryxis.eryxis.repository.TransazioniRepository;
import com.eryxis.eryxis.service.*;
import com.eryxis.eryxis.service.externalAPI.AzioniService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    @Autowired
    private ContiService contiService;
    @Autowired
    private CarteController carteController;
    @Autowired
    private CarteRepository carteRepository;
    @Autowired
    private TransazioniRepository transazioniRepository;

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
                    alert("Carta non trovata!", "Carta non trovata! Riprova più tardi.");
                    return "redirect:/home";
                }
            } else {
                alert("Sessione scaduta!", "Sessione scaduta! Eseguire nuovamente l'accesso.");
                return "redirect:/home";
            }
        }
        else{
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @PostMapping("/submit-recharge")
    public String recharge(Model model, @RequestParam("importo") float importo, @RequestParam("carte1") String carte1, @RequestParam("carte2") String carte2) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken customAuth) {
            List<Carte> carte = customAuth.getCarte();

            if(carte1.equals(carte2)) {
                alert("Carte corrispondenti", "Carte corrispondenti! Le carte selezionate corrispondono! Selezionare due carte diverse per eseguire l'operazione");
                return "redirect:/home";
            }
            else if (carte != null) {
                if (carte1.equals("conto") || carte2.equals("conto")) {
                    if (carte.get(0).getConto() != null) {
                        Conti conto = carte.get(0).getConto();

                        if (carte.stream().anyMatch(c -> c.getTipo().equals(carte2))) {
                            if (conto.getSaldo() >= importo) {
                                Carte cartaD = carte.stream().filter(c -> c.getTipo().equals(carte2)).findFirst().get();

                                conto.setSaldo(conto.getSaldo() - importo);
                                contiService.save(conto);
                                cartaD.setSaldoDisponibile(cartaD.getSaldoDisponibile() + importo);
                                carteRepository.save(cartaD);
                            }
                            else{
                                alert("Saldo Disponibile Insufficiente!", "Saldo Disponibile Insufficiente! L'importo inserito è maggiore del saldo disponibile sul conto da cui scaricare!");
                                return "redirect:/home";
                            }
                        }
                        else if (carte.stream().anyMatch(c -> c.getTipo().equals(carte1))) {
                            Carte carteS = carte.stream().filter(c -> c.getTipo().equals(carte1)).findFirst().get();
                            if (carteS.getSaldoDisponibile() >= importo) {

                                conto.setSaldo(conto.getSaldo() + importo);
                                contiService.save(conto);
                                carteS.setSaldoDisponibile(carteS.getSaldoDisponibile() - importo);
                                carteRepository.save(carteS);
                            }
                            else{
                                alert("Saldo Disponibile Insufficiente!", "Saldo Disponibile Insufficiente! L'importo inserito è maggiore del saldo disponibile sulla carta da cui scaricare!");
                                return "redirect:/home";
                            }
                        }
                    }
                }
                else if (carte.stream().anyMatch(c -> c.getTipo().equals(carte1))) {
                    Carte cartaS = carte.stream().filter(c -> c.getTipo().equals(carte1)).findFirst().get();
                    if (cartaS.getSaldoDisponibile() >= importo) {

                        if (carte.stream().anyMatch(c -> c.getTipo().equals(carte2))) {
                            Carte cartaD = carte.stream().filter(c -> c.getTipo().equals(carte2)).findFirst().get();

                            cartaS.setSaldoDisponibile(cartaS.getSaldoDisponibile() - importo);
                            carteRepository.save(cartaS);
                            cartaD.setSaldoDisponibile(cartaD.getSaldoDisponibile() + importo);
                            carteRepository.save(cartaD);
                        }
                    }
                    else{
                        alert("Saldo Disponibile Insufficiente!", "Saldo Disponibile Insufficiente! L'importo inserito è maggiore del saldo disponibile sulla carta da cui scaricare!");
                        return "redirect:/home";
                    }
                }
                else {
                    alert("Carta non trovata!", "Carta non trovata! Riprova più tardi.");
                    return "redirect:/home";
                }

                if (carte.get(0).getConto() != null) {
                    Transazioni transazioni = new Transazioni();
                    transazioni.setImporto(importo);
                    transazioni.setDestinatario("Recharged Card");
                    transazioni.setCausale("ricarica");
                    transazioni.setConto(carte.get(0).getConto());
                    transazioni.setTipo("versamento");
                    transazioniRepository.save(transazioni);
                }
                else{
                    alert("Sessione scaduta!", "Sessione scaduta! Eseguire nuovamente l'accesso.");
                    return "redirect:/login";
                }
            }
            else{
                alert("Sessione scaduta!", "Sessione scaduta! Eseguire nuovamente l'accesso.");
                return "redirect:/login";
            }
        }

        return "redirect:/home";
    }


    @PostMapping("/alert")
    public void alert(@RequestParam String header, @RequestParam String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
