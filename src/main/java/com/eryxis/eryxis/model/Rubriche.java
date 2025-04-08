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
public class Rubriche {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idContatto;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @Column(nullable = false, length = 27, unique = true)
    private String IBAN;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utenti utente;
}