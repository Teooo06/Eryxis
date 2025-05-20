package com.eryxis.eryxis.service.externalAPI;

import com.eryxis.eryxis.model.ExchangeRate;
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


}
