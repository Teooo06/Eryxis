package com.eryxis.eryxis.service;

import com.eryxis.eryxis.model.Investimenti;
import com.eryxis.eryxis.model.ValoriAzioni;
import com.eryxis.eryxis.model.ValoriAzioniId;
import com.eryxis.eryxis.repository.InvestimentiRepository;
import com.eryxis.eryxis.repository.ValoriAzioniRepository;
import com.eryxis.eryxis.service.externalAPI.AzioniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ValoriAzioniService {
    @Autowired
    private ValoriAzioniRepository valoriAzioniRepository;

    @Autowired
    private InvestimentiRepository investimentiRepository;

    @Autowired
    private AzioniService azioniService;

    /**
     * Metodo schedulato per aggiornare i valori delle azioni ogni giorno a mezzanotte.
     * Recupera tutti gli investimenti e aggiorna i loro valori in base al valore corrente delle azioni.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateDailyInvestmentValues() {
        // Get all investments
        List<Investimenti> investimentiList = investimentiRepository.findAll();

        // Current date
        LocalDate today = LocalDate.now();

        for (Investimenti investimento : investimentiList) {
            // Check if a record for this investment and date already exists
            if (!valoriAzioniRepository.existsById(new ValoriAzioniId(investimento.getIdInvestimento(), today))) {
                // Get the stock value for the current day
                BigDecimal stockValue = azioniService.getStockValueForDate(investimento.getSymbol(), today);

                if (stockValue != null) {
                    // Calculate the total value of the investment
                    BigDecimal totalValue = stockValue.multiply(BigDecimal.valueOf(investimento.getQuantita()));

                    // Create a new ValoriAzioni entry
                    ValoriAzioni valoriAzioni = new ValoriAzioni();
                    valoriAzioni.setIdInvestimento(investimento.getIdInvestimento());
                    valoriAzioni.setDataValore(today);
                    valoriAzioni.setValore(totalValue.doubleValue());

                    // Save the entry to the database
                    valoriAzioniRepository.save(valoriAzioni);
                }
            }
        }
    }

    public List<ValoriAzioni> getByListInvestimenti(List<Investimenti> investimenti){
        List<Integer> ids = investimenti.stream()
                                        .map(Investimenti::getIdInvestimento)
                                        .toList();
        return valoriAzioniRepository.findByIdInvestimentoIn(ids);
    }

    public ValoriAzioni getByIdInvestimento(int idInvestimento){return valoriAzioniRepository.findByIdInvestimento(idInvestimento);}

    public void deleteByValoreAzioni(ValoriAzioni valoriAzioni){valoriAzioniRepository.deleteValoriAzioni(valoriAzioni);}
    public ValoriAzioni save(ValoriAzioni valoriAzioni){return valoriAzioniRepository.save(valoriAzioni);}

}
