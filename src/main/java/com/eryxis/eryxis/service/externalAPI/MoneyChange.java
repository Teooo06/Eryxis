package com.eryxis.eryxis.service.externalAPI;

import com.eryxis.eryxis.model.Azioni;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.ExchangeRate;
import com.eryxis.eryxis.model.Investimenti;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class MoneyChange {

    private static final String ECB_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public List<ExchangeRate> getExchangeRates() {
        List<ExchangeRate> rates = new ArrayList<>();

        try {
            InputStream xmlStream = new URL(ECB_URL).openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true); // Necessario per l'XML della BCE
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlStream);

            NodeList cubeList = doc.getElementsByTagNameNS("*", "Cube");

            // Aggiungo manualmente l'euro con valore 1.0
            rates.add(new ExchangeRate("EUR", 1.0));

            for (int i = 0; i < cubeList.getLength(); i++) {
                Element cube = (Element) cubeList.item(i);
                if (cube.hasAttribute("currency") && cube.hasAttribute("rate")) {
                    String currency = cube.getAttribute("currency");
                    double rate = Double.parseDouble(cube.getAttribute("rate"));
                    rates.add(new ExchangeRate(currency, rate));
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // Oppure loggalo
        }

        return rates;
    }

    public List<ExchangeRate> getExchangeRatesBasedOn(String baseCurrency) {
        List<ExchangeRate> originalRates = getExchangeRates();
        List<ExchangeRate> adjustedRates = new ArrayList<>();

        double baseRate = -1;

        for (ExchangeRate rate : originalRates) {
            if (rate.getCurrency().equalsIgnoreCase(baseCurrency)) {
                baseRate = rate.getRate();
                break;
            }
        }

        if (baseRate == -1) {
            throw new IllegalArgumentException("Valuta base non trovata: " + baseCurrency);
        }

        for (ExchangeRate rate : originalRates) {
            double adjusted = rate.getRate() / baseRate;
            adjustedRates.add(new ExchangeRate(rate.getCurrency(), adjusted));
        }

        return adjustedRates;
    }

    /**
     * Ottiene il fattore di conversione tra la valuta del conto e la valuta target.
     *
     * @param accountCurrency La valuta del conto.
     * @param targetCurrency  La valuta target.
     * @return Il fattore di conversione.
     */
    private double getConversionFactor(String accountCurrency, String targetCurrency) {
        // Fetch exchange rates
        List<ExchangeRate> exchangeRates = getExchangeRates();

        // Find the exchange rate for the account's currency
        double accountCurrencyRate = exchangeRates.stream()
                .filter(rate -> rate.getCurrency().equalsIgnoreCase(accountCurrency))
                .map(ExchangeRate::getRate)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account currency not found: " + accountCurrency));

        // Find the exchange rate for the target currency
        double targetCurrencyRate = exchangeRates.stream()
                .filter(rate -> rate.getCurrency().equalsIgnoreCase(targetCurrency))
                .map(ExchangeRate::getRate)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Target currency not found: " + targetCurrency));

        // Return the conversion factor
        return accountCurrencyRate / targetCurrencyRate;
    }

    /**
     * Converte un valore in dollari in una valuta target.
     *
     * @param targetCurrency La valuta target.
     * @param valueInDollars Il valore in dollari da convertire.
     * @param conto          Il conto da cui prelevare la valuta.
     * @return Il valore convertito nella valuta target.
     */
    public double convertCurrency(String targetCurrency, double valueInDollars, Conti conto) {
        // Get the account's currency
        String accountCurrency = conto.getValuta();

        // Get the conversion factor using the helper method
        double conversionFactor = getConversionFactor(accountCurrency, targetCurrency);

        // Convert the value from USD to the target currency
        return valueInDollars * conversionFactor;
    }

    /**
     * Converte una lista di Azioni in una valuta target.
     *
     * @param targetCurrency La valuta target.
     * @param azioniList    La lista di Azioni da convertire.
     * @param conto         Il conto da cui prelevare la valuta.
     * @return La lista di Azioni convertite nella valuta target.
     */
    public List<Azioni> convertCurrencyForAzioniList(String targetCurrency, List<Azioni> azioniList, Conti conto) {
        double conversionFactor = getConversionFactor(conto.getValuta(), targetCurrency);

        // Convert the price for each Azioni
        for (Azioni azione : azioniList) {
            azione.setPrice(azione.getPrice() * conversionFactor);
        }

        return azioniList;
    }

    /**
     * Converte una lista di Investimenti in una valuta target.
     *
     * @param targetCurrency La valuta target.
     * @param investimentiList La lista di Investimenti da convertire.
     * @param conto          Il conto da cui prelevare la valuta.
     * @return La lista di Investimenti convertiti nella valuta target.
     */
    public List<Investimenti> convertCurrencyForInvestimentiList(String targetCurrency, List<Investimenti> investimentiList, Conti conto) {
        double conversionFactor = getConversionFactor(conto.getValuta(), targetCurrency);

        // Convert the prezzoAcquisto for each Investimenti
        for (Investimenti investimento : investimentiList) {
            investimento.setPrezzoAcquisto(investimento.getPrezzoAcquisto() * conversionFactor);
        }

        return investimentiList;
    }

}
