package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.*;
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

import java.util.Comparator;
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
    @Autowired
    private TransazioniService transazioniService;
    @Autowired
    private ContiService contiService;
    @Autowired
    private CarteService carteService;

    @GetMapping("/trading")
    public String trading(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        int page = 0;

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            Utenti utente = utentiService.findByIdUtente(id);

            List<Investimenti> investimenti = investimentiService.findByUtente(utente);
            List<Azioni> azioni = azioniService.getListAzioni(0,  49);

            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("listaAzioni", azioni);
            model.addAttribute("investimenti", investimenti);
            model.addAttribute("page", page);

            return "trading";
        }

        return "redirect:/login";
    }

    @GetMapping("/search")
    public String trade(Model model,@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                    @RequestParam(name = "filtro", required = false, defaultValue = "") String filtro) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            Utenti utente = utentiService.findByIdUtente(id);

            List<Investimenti> investimenti = investimentiService.findByUtente(utente);
            List<Azioni> azioni;

            if(!filtro.isEmpty()){
                azioni = azioniService.cercaAzioniPerSymbol(filtro);
                model.addAttribute("page", -1);
            }
            else if (page != 0) {
                azioni = azioniService.getListAzioni(page * 50, (page * 50) + 49);
                model.addAttribute("page", page + 1);
            }
            else {
                return null;
            }

            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("listaAzioni", azioni);
            model.addAttribute("investimenti", investimenti);

            return "trading";
        }

        return "redirect:/login";
    }


    @PostMapping("/dettaglio-azione")
    public String dettaglioAzione(Model model,
                                  @RequestParam("symbol") String symbol,
                                  @RequestParam("exchangeShortName") String exchangeShortName,
                                  @RequestParam("name") String name,
                                  @RequestParam("price") float price,
                                  @RequestParam("type") String type,
                                  @RequestParam("exchange") String exchange) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();
            List<Carte> carte = customAuth.getCarte();
            // Ottieni ulteriori dettagli dell'azione se necessario
            Histories histories = azioniService.getDatiAzione(symbol);

            // Ordina le storie dalla più vecchia alla più recente
            if (histories != null && histories.getHistorical() != null) {
                histories.getHistorical().sort(Comparator.comparing(Histories.HistoricalEntry::getDate));
            }

            // Aggiungi i dati al modello
            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("symbol", symbol);
            model.addAttribute("name", name);
            model.addAttribute("exchange", exchange);
            model.addAttribute("exchangeShortName", exchangeShortName);
            model.addAttribute("price", price);
            model.addAttribute("type", type);
            model.addAttribute("historyData", histories);
            model.addAttribute("cardCount", carte.size());

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

    @PostMapping("/buyStock")
    public String buyStock(@RequestParam("symbol") String symbol,
                           @RequestParam("importo") float importo,
                           @RequestParam("carte") String carta,
                           Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            List<Carte> carte = customAuth.getCarte();
            Utenti utente = utentiService.findByIdUtente(id);

            Azioni azione = azioniService.getAzione(symbol);

            Conti conto = carte.get(0).getConto();

            // Controllo se il saldo è sufficiente
            if (carta.equals("conto")) {
                if (conto.getSaldo() < importo) {
                    //model.addAttribute("error", "Saldo insufficiente nel conto.");
                    return "redirect:/trading";
                }
            } else {
                boolean saldoSufficiente = false;
                for (Carte cart : carte) {
                    if (cart.getTipo().equals(carta) && cart.getSaldoDisponibile() >= importo) {
                        saldoSufficiente = true;
                        break;
                    }
                }
                if (!saldoSufficiente) {
                    //model.addAttribute("error", "Saldo insufficiente nella carta selezionata.");
                    return "redirect:/trading";
                }
            }

            // Creazione e salvataggio dell'investimento
            Investimenti investimento = new Investimenti();
            investimento.setSymbol(symbol);
            investimento.setNomeAzione(azione.getName());
            investimento.setPrezzoAcquisto(azione.getPrice());
            investimento.setQuantita((int) (importo / azione.getPrice()));
            investimento.setUtente(utente);
            investimentiService.save(investimento);

            // Creazione e salvataggio della transazione
            Transazioni transazione = new Transazioni();
            transazione.setImporto(-importo);
            transazione.setTipo("addebito");
            transazione.setDestinatario(symbol);
            transazione.setCausale("Acquisto di azioni");
            transazione.setConto(conto);
            transazioniService.save(transazione);

            // Aggiornamento del saldo
            if (carta.equals("conto")) {
                conto.setSaldo(conto.getSaldo() - importo);
                contiService.save(conto);
            } else {
                for (Carte cart : carte) {
                    if (cart.getTipo().equals(carta)) {
                        cart.setSaldoDisponibile(cart.getSaldoDisponibile() - importo);
                        carteService.save(cart);
                        break;
                    }
                }
            }

            return "redirect:/trading";
        }

        return "redirect:/login";
    }
}
