package com.eryxis.eryxis.service.externalAPI;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eryxis.eryxis.model.Azioni;
import com.eryxis.eryxis.model.Histories;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AzioniService {

    private final String api_key = "ciao";
    private final RestTemplate restTemplate = new RestTemplate();

    // funzione che richiama l'API e salva tutte le azioni in un Json locale
    @Scheduled(fixedRate = 1800000)
    public void salvaJsonDaApi() {
        String url = "https://financialmodelingprep.com/api/v3/stock/list?apikey=" + api_key;
        String jsonResponse = restTemplate.getForObject(url, String.class);

        if (jsonResponse == null) {
            System.out.println("Errore durante la chiamata all'API");
            return;
        }

        try (FileWriter writer = new FileWriter("stocks.json")) {
            writer.write(jsonResponse);
        } catch (IOException e) {
            System.out.println("Errore durante la scrittura del file: " + e.getMessage());
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("salvaJsonDaApi eseguito alle: " + now.format(formatter));
    }

    // funzione che legge il file json locale e ritorna una lista prendendo i file da n1 a n2 presi dall'utente
    public List<Azioni> getListAzioni(int posInizio, int posFine) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Azioni> tutteAzioni = objectMapper.readValue(new File("stocks.json"), new TypeReference<List<Azioni>>() {});
            if (posInizio < 0 || posFine > tutteAzioni.size() || posInizio >= posFine) {
                throw new IllegalArgumentException("Intervallo non valido");
            }
            return tutteAzioni.subList(posInizio, posFine);
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file JSON: " + e.getMessage());
            return List.of(); // ritorna lista vuota in caso di errore
        }
    }

    // funzione che ritorna i valori della azienda specifica per una data lunghezza di tempo
    public Histories getDatiAzione(String symbol, int giorni) {
        String url = "https://financialmodelingprep.com/api/v3/historical-price-full/" + symbol + "?serietype=line&timeseries=" + giorni + "&apikey=" + api_key;
        String jsonResponse = restTemplate.getForObject(url, String.class);

        if (jsonResponse == null) {
            System.out.println("Errore durante la chiamata all'API per " + symbol);
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonResponse, Histories.class);
        } catch (IOException e) {
            System.out.println("Errore durante il parsing della risposta JSON per " + symbol + ": " + e.getMessage());
            return null;
        }
    }

}
