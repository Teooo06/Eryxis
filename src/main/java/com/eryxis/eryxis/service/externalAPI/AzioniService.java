package com.eryxis.eryxis.service.externalAPI;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eryxis.eryxis.model.Azioni;
import com.eryxis.eryxis.model.Histories;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class AzioniService {

    @Value("${api.key}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Chiama l'API di Financial Modeling Prep per ottenere la lista di tutte le azioni
     * disponibili e salva la risposta JSON in un file locale chiamato "stocks.json".
     * Questa funzione viene eseguita automaticamente ogni 30 minuti grazie all'annotazione @Scheduled.
     */
    @Scheduled(fixedRate = 1800000)
    public void salvaJsonDaApi() {
        String url = "https://financialmodelingprep.com/api/v3/stock/list?apikey=" + apiKey;
        String jsonResponse = restTemplate.getForObject(url, String.class);

        if (jsonResponse == null) {
            System.out.println("Errore durante la chiamata all'API");
            return;
        }

        try {
            // Parsing JSON ricevuto
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> allStocks = mapper.readValue(jsonResponse, new TypeReference<>() {});

            // Filtraggio per exchange gratuito
            List<Map<String, Object>> filteredStocks = allStocks.stream()
                    .filter(stock -> {
                        Object exchange = stock.get("exchangeShortName");
                        return exchange != null &&
                                (exchange.equals("NYSE") || exchange.equals("NASDAQ") || exchange.equals("AMEX"));
                    })
                    .toList();

            // Scrittura JSON filtrato su file
            try (FileWriter writer = new FileWriter("stocks.json")) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, filteredStocks);
            }

            // Log
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("salvaJsonDaApi (filtrata) eseguito alle: " + now.format(formatter));
        } catch (IOException e) {
            System.out.println("Errore durante il parsing o la scrittura del file: " + e.getMessage());
        }
    }

    /**
     * Legge il file JSON locale "stocks.json" che contiene una lista di azioni,
     * e restituisce una sottolista compresa tra le posizioni specificate.
     *
     * @param posInizio la posizione iniziale (inclusiva) nella lista
     * @param posFine   la posizione finale (esclusiva) nella lista
     * @return una lista di oggetti {@link Azioni} tra le posizioni indicate
     * @throws IllegalArgumentException se l'intervallo è invalido
     */
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

    /**
     * Legge il file JSON locale "stocks.json" che contiene una lista di azioni,
     * e restituisce l'oggetto {@link Azioni} corrispondente al simbolo specificato.
     *
     * @param symbol il simbolo dell'azione da cercare
     * @return l'oggetto {@link Azioni} corrispondente, oppure {@code null} se non trovato
     */
    public Azioni getAzione(String symbol) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Azioni> tutteAzioni = objectMapper.readValue(new File("stocks.json"), new TypeReference<List<Azioni>>() {});
            for (Azioni azione : tutteAzioni) {
                if (azione.getSymbol().equalsIgnoreCase(symbol)) {
                    return azione;
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file JSON: " + e.getMessage());
        }
        return null; // ritorna null se non trovata
    }

    /**
     * Recupera i dati storici di una singola azione dal server FMP, limitati a un certo numero di giorni.
     * I dati includono la data e il prezzo di chiusura.
     *
     * @param symbol il simbolo dell'azione (es. "AAPL")
     * @param giorni
     * @return un oggetto {@link Histories} contenente il simbolo e la lista di valori storici
     */
    public Histories getDatiAzione(String symbol, int giorni) {
        String url = "https://financialmodelingprep.com/api/v3/historical-price-full/" + symbol + "?serietype=line&apikey=" + apiKey;
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

    public BigDecimal getStockValueForDate(String symbol, LocalDate date) {
        Histories histories = getDatiAzione(symbol, 1); // Fetch historical data for the symbol
        if (histories != null && histories.getHistorical() != null) {
            return histories.getHistorical().stream()
                    .filter(h -> h.getDate().equals(date.toString()))
                    .map(h -> BigDecimal.valueOf(h.getClose()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

}
