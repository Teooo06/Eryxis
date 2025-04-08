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
public class Permessi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPermesso;

    @Column(nullable = false, length = 10)
    private String ruolo;

    @Column(nullable = false, length = 2)
    private String codicePermesso;
}