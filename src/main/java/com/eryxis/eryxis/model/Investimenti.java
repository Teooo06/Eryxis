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
public class Investimenti {
    @Id
    @Column(length = 12)
    private String ISIN;

    @Column(nullable = false, length = 50)
    private String settore;

    @Column(nullable = false, length = 50)
    private String divisa;

    @Column(nullable = false, length = 11)
    private String tipo;

    @Column(nullable = false, length = 50)
    private String nomeTitolo;

    @Column(nullable = false, length = 255)
    private String descrizione;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private java.sql.Date dataAcquisto;

    @Column(columnDefinition = "DECIMAL(20, 2) DEFAULT 0")
    private double quantitaDenaro;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int quantitaTotale;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utenti utente;
}