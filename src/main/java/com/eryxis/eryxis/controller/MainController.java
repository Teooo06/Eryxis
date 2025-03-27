package com.eryxis.eryxis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false)  String msg) {
        model.addAttribute("msg", msg);
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        // Verifica credenziali (qui dovresti fare un controllo sul DB quando lo implemento)
        boolean isValidUser = true;

        if (isValidUser) {
            // Redirect alla chiamata dell'endpoint /auth/send-otp con l'email come parametro


            // Reindirizza alla pagina OTP
            return "redirect:/auth/otp?email=" + email;

        }
        // da gestire poi l'errore
        return "redirect:/login";
    }





}
