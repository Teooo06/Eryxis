package com.eryxis.eryxis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ValoriAzioniId.class)
public class ValoriAzioni {

    @Id
    private int idInvestimento;

    @Id
    @Column(nullable = false)
    private LocalDate dataValore;

    @Column(nullable = false, columnDefinition = "DECIMAL(10, 2)")
    private double valore;
}
