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
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idLog;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp dataModifica;

    @Column(nullable = false, length = 50)
    private String tipoModifica;

    @Column(nullable = false, length = 255)
    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utenti utente;
}