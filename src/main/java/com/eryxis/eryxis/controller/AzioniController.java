package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Azioni;
import com.eryxis.eryxis.model.Histories;
import com.eryxis.eryxis.service.externalAPI.AzioniService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quote")
public class AzioniController {
    @Autowired
    private AzioniService azioniService;

    // funzione GET per richiedere la lista delle azioni
    @GetMapping("/azioni")
    public List<Azioni> getAzioni(
            @RequestParam int posInizio,
            @RequestParam int posFine) {
        return azioniService.getListAzioni(posInizio, posFine);
    }

    @GetMapping("/azione")
    public Histories getDatiAzione(
            @RequestParam String symbol) {
        return azioniService.getDatiAzione(symbol);
    }
}
