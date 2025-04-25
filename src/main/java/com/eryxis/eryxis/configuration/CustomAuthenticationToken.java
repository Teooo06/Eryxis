package com.eryxis.eryxis.configuration;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private int idUtente;
    private String nome;
    private String cognome;

    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, int idUtente, String nome, String cognome) {
        super(principal, credentials, authorities);
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
    }
}
