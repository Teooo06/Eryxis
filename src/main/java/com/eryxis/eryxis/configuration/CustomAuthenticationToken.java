package com.eryxis.eryxis.configuration;

import com.eryxis.eryxis.model.Carte;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private int idUtente;
    private String nome;
    private String cognome;
    private List<Carte> carte;

    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, int idUtente, String nome, String cognome, List<Carte> carte) {
        super(principal, credentials, authorities);
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.carte = carte;
    }
}
