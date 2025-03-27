package com.eryxis.eryxis.controller;
import com.eryxis.eryxis.service.OTPService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth") // http://localhost:8080/auth/send-otp
public class AuthController {
    private final OTPService otpService;

    public AuthController(OTPService otpService) {
        this.otpService = otpService;
    }

    // Metodo iniziale che ritorna la pagina per l'inserimento dell'OTP
    @GetMapping("/otp")
    public String otpPage(@RequestParam String email, Model model) {
        otpService.generateOTP(email);
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
        if (otpService.validateOTP(email, otp)) {
            return ResponseEntity.ok("Accesso consentito");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP non valido");
    }

}

