package com.eryxis.eryxis.controller;
import com.eryxis.eryxis.service.OTPService;
import lombok.RequiredArgsConstructor;
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
    private final OTPService otpService;

    // Metodo iniziale che ritorna la pagina per l'inserimento dell'OTP
    @GetMapping("/otp")
    public String otpPage(@RequestParam String email, Model model) {
        boolean otpGoogle = false;

        if (otpGoogle) {

        }else {
            otpService.generateOTP(email);
        }
        model.addAttribute("email", email);
        return "otp";
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOTP(@RequestParam String email) {
        otpService.generateOTP(email);
        return ResponseEntity.ok("OTP inviato all'email " + email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        boolean otpGoogle = true;


        if (otpGoogle) {
            return null;

        }else {
            if (otpService.validateOTP(email, otp)) {
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

