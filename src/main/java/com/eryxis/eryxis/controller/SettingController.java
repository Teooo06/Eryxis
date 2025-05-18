package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SettingController {

    @GetMapping("/setting")
    public String setting(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato correttamente con CustomAuthenticationToken
        if (auth instanceof CustomAuthenticationToken customAuth) {
            int id = customAuth.getIdUtente();
            String nome = customAuth.getNome();
            String cognome = customAuth.getCognome();

            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);

            return  "setting";
        }

        return "redirect:/login";
    }
}
