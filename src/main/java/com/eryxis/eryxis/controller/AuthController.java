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

        /* IN questo caso ci sono 2 possibilità:
         * 1. L'utente ha l'OTP attivo e quindi deve inserire il codice OTP generato con Google Authenticator
         * 2. L'utente non ha l'OTP attivo e quindi devo mandare la mail

        OTP 1 => Google Authenticator
        OTP 2 => Mail
        */
        if (!utentiService.useOTP(email)) {
            // Dovrebbe mandare non la mail ma la passphrase
            otpService.generateOTPa(email);
        }

        model.addAttribute("email", email);
        return "verifyCodePage";
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp, Model model) {

        // Se l'utente OTP == 1 => Google Authenticator
        if (!utentiService.useOTP(email)) {
            // Se l'utente non ha l'OTP attivo significa che l'ho mandato prima per mail la mail

            if (otpService.validateOTP(email, otp)) {
                Utenti utente = utentiService.findByMail(email);
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/home?id=" + utente.getIdUtente() +
                                        "&nome=" + utente.getNome() +
                                        "&cognome=" + utente.getCognome())
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

