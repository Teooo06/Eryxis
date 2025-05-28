package com.eryxis.eryxis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tickets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTicket;

    @Column(nullable = false, length = 20)
    private String titolo;

    @Column(nullable = false, length = 500)
    private String descrizione;

    @Column(nullable = false)
    private short stato;

    @Column(nullable = false, columnDefinition="DEFAULT CURRENT_TIMESTAMP")
    private Date dataApertura;

    @Column(nullable = false, columnDefinition="DEFAULT CURRENT_TIMESTAMP")
    private Date dataChiusura;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utenti utente;
}