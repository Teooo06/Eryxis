package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final OTPService otpService;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false)  String msg) {
        model.addAttribute("msg", msg);
        return "login";
    }

    // Homepage dopo la verifica dell'OTP
    @GetMapping("/home")
    public String home() {
        return "index"; // Ritorna la vista home.html o home.jsp
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        // Verifica credenziali (qui dovresti fare un controllo sul DB quando lo implemento)
        boolean isValidUser = true;
        boolean otpValid = true;

        if (isValidUser) {
            // Reindirizza alla pagina OTP
            String redirectUrl = "/auth/otp?email=" + email;
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();

        }

        // Reindirizza alla pagina di login con messaggio di errore
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.LOCATION, "/?msg=Credenziali errate")
                .build();
    }

}
