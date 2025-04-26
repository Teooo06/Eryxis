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
public class Transazioni {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTransazione;

    @Column(columnDefinition = "DECIMAL(20, 2) DEFAULT 0")
    private double importo;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp dataTransazione;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String destinatario;

    @ManyToOne
    @JoinColumn(name = "IBAN", nullable = false)
    private Conti conto;
}