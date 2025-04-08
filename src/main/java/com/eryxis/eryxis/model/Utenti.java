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
public class Utenti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUtente;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, length = 50)
    private String cognome;

    @Column(nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private java.sql.Date dataNascita;

    @Column(nullable = false, length = 100)
    private String indirizzo;

    @Column(nullable = false, length = 16, unique = true)
    private String codiceFiscale;

    @Column(nullable = false, length = 150, unique = true)
    private String mail;

    @Column(nullable = false, length = 6)
    private String prefisso;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(nullable = false, length = 100, unique = true)
    private String password;

    @Column(nullable = false, length = 255, unique = true)
    private String passPhrase;

    @ManyToOne
    @JoinColumn(name = "id_permesso", nullable = false)
    private Permessi permesso;
}