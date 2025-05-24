package com.eryxis.eryxis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Carte {
    @Id
    @Column(length = 16)
    private String numeroCarta;

    @Column(length = 4, unique = true, nullable = false)
    private String CVV;

    @Column(nullable = false)
    private java.sql.Date dataScadenza;

    @Column(length = 5, unique = true, nullable = false)
    private String PIN;

    @Column(length = 9, nullable = false)
    private String tipo;

    @Column(columnDefinition = "DECIMAL(20, 2) DEFAULT 0")
    private double saldoDisponibile;

    @Column(columnDefinition = "DECIMAL(20, 2) DEFAULT 0")
    private double saldoContabile;

    @ManyToOne
    @JoinColumn(name = "IBAN", nullable = false)
    private Conti conto;
}