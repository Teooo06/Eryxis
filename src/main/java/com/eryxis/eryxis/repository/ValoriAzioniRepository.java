package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Investimenti;
import com.eryxis.eryxis.model.ValoriAzioni;
import com.eryxis.eryxis.model.ValoriAzioniId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ValoriAzioniRepository extends JpaRepository<ValoriAzioni, ValoriAzioniId> {

    ValoriAzioni findByIdInvestimentoAndDataValore(int idInvestimento, LocalDate dataValore);

    List<ValoriAzioni> findByIdInvestimentoIn(List<Integer> symbols);
}
