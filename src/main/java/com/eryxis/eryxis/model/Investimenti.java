package com.eryxis.eryxis.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Investimenti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idInvestimento;

    @Column(length = 12, nullable = false)
    private String symbol;

    @Column(length = 500, nullable = false)
    private String nomeAzione;

    @Column(nullable = false, columnDefinition = "DECIMAL(10, 2)")
    private double prezzoAcquisto;

    @Column(nullable = false)
    private int quantita;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp dataAcquisto;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utenti utente;
}