package com.eryxis.eryxis.controller;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.Security.OTPService;
import com.eryxis.eryxis.service.UtentiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth") // http://localhost:8080/auth/send-otp
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private OTPService otpService;
    @Autowired
    private UtentiService utentiService;

    // Metodo iniziale che ritorna la pagina per l'inserimento dell'OTP
    @GetMapping("/otp")
    public String otpPage(@RequestParam String email, Model model) {

        if (utentiService.useOTP(email)) {
            otpService.generateOTP(email);
        }
        model.addAttribute("email", email);
        return "verifyCodePage";
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp, Model model) {

        if (utentiService.useOTP(email)) {

            if (otpService.validateOTP(email, otp)) {
                Utenti utente = utentiService.findByMail(email);
                model.addAttribute("id", utente.getIdUtente());
                model.addAttribute("nome", utente.getNome());
                model.addAttribute("cognome", utente.getCognome());
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/home")
                        .build();
            }
            // OTP errato: Resta nella pagina OTP con un messaggio di errore
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.LOCATION, "/auth/otp?email=" + email + "&msg=OTP%20non%20valido")
                    .build();

        }else {
            if (otpService.validateOTPGoogle(email, otp)) {
                Utenti utente = utentiService.findByMail(email);
                model.addAttribute("id", utente.getIdUtente());
                model.addAttribute("nome", utente.getNome());
                model.addAttribute("cognome", utente.getCognome());
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/home")
                        .build();
            }
            // OTP errato: Resta nella pagina OTP con un messaggio di errore
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.LOCATION, "/auth/otp?email=" + email + "&msg=OTP%20non%20valido")
                    .build();
        }
    }
}

