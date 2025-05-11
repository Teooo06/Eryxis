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
public class Finanziamenti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFinanziamento;

    @Column(nullable = false, length = 8)
    private String tipo;

    @Column(columnDefinition = "DECIMAL(20, 2) DEFAULT 0")
    private double importo;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private java.sql.Date dataErogazione;

    @Column(columnDefinition = "DECIMAL(15, 2) DEFAULT 0")
    private double interessi;

    @Column(columnDefinition = "DECIMAL(5, 2) DEFAULT 2.5")
    private double spesaIncasso;

    @Column(nullable = false, length = 20)
    private String tipoRata;

    @Column(columnDefinition = "DECIMAL(10, 2) DEFAULT 0")
    private double valoreRata;

    @Column(nullable = false)
    private java.sql.Date inizioPagamento;

    @Column(columnDefinition = "DECIMAL(15, 2) DEFAULT 0")
    private double importoPagato;

    @Column(nullable = false, length = 250)
    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utenti utente;
}