package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.configuration.CustomAuthenticationToken;
import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.CarteService;
import com.eryxis.eryxis.service.ContiService;
import com.eryxis.eryxis.service.Security.OTPService;
import com.eryxis.eryxis.service.UtentiService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/auth") // http://localhost:8080/auth/send-otp
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private OTPService otpService;
    @Autowired
    private UtentiService utentiService;
    @Autowired
    private ContiService contiService;
    @Autowired
    private CarteService carteService;

    // Metodo iniziale che ritorna la pagina per l'inserimento dell'OTP
    @GetMapping("/otp")
    public String otpPage(@RequestParam String email, Model model) {
        if (!utentiService.useOTP(email)) {
            // Dovrebbe mandare non la mail ma la passphrase
            otpService.generateOTPa(email);
        }

        model.addAttribute("email", email);
        return "verifyCodePage";
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp, Model model, HttpServletRequest request) {

        boolean isGoogleOTP = utentiService.useOTP(email);
        boolean isValidOTP = isGoogleOTP ?
                otpService.validateOTPGoogle(email, otp) :
                otpService.validateOTP(email, otp);

        if (isValidOTP) {
            // Recupera i dati dell'utente
            Utenti utente = utentiService.findByMail(email);
            List<Conti> conto = contiService.findByUtente(utente);
            List<Carte> carte = carteService.findByConto(conto.get(0));

            // Crea un oggetto UserDetails (senza password, perché non serve qui)
            CustomAuthenticationToken authToken = getCustomAuthenticationToken(email, utente, carte);

            // Crea un SecurityContext e imposta il token
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);

            // Imposta nel SecurityContextHolder
            SecurityContextHolder.setContext(context);

            // Salva nel contesto della sessione, per non perderlo
            HttpSession session = request.getSession(true); // crea la sessione se non esiste
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            // Reindirizza alla home SENZA parametri nella URL
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/home")
                    .build();
        }

        // OTP errato: reindirizza alla pagina OTP con messaggio di errore
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.LOCATION, "/auth/otp?email=" + email + "&msg=OTP%20non%20valido")
                .build();
    }

    private static CustomAuthenticationToken getCustomAuthenticationToken(String email, @NotNull Utenti utente, @NotNull List<Carte> carte) {
        UserDetails userDetails = new User(email, "", List.of()); // puoi inserire ruoli se li gestisci

        // Crea il token di autenticazione personalizzato
        CustomAuthenticationToken authToken = new CustomAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities(),
                utente.getIdUtente(),
                utente.getNome(),
                utente.getCognome(),
                carte
        );
        return authToken;
    }

}

