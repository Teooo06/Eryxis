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
public class Conti {
    @Id
    @Column(length = 27)
    private String IBAN;

    @Column(columnDefinition = "DECIMAL(20, 2) DEFAULT 0")
    private double saldo;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean stato;

    @Column(nullable = false, length = 3)
    private String valuta;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private java.sql.Date dataApertura;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utenti utente;

    @ManyToOne
    @JoinColumn(name = "id_consulente", nullable = false)
    private Utenti consulente;
}
