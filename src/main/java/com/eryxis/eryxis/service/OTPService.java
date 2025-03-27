package com.eryxis.eryxis.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {
    private final JavaMailSender mailSender;
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public OTPService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String generateOTP(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 cifre
        otpStorage.put(email, otp);
        sendOTPEmail(email, otp);
        return otp;
    }

    private void sendOTPEmail(String email, String otp) {
        String htmlContent = "<html><body>"
                + "<p>Caro client,</p>"
                + "<p>Il tuo codice OTP è: " + otp + "</p>"
                + "<br><br>"
                + "<p>--</p>"
                + "<p>Se la mail non la riguarda ignori il messaggio</p>"
                + "<p>--</p>"
                + "<p>Eryxis Bank</p>"
                + "<p>Soluzioni bancarie sicure e moderne</p>"
                + "<p>info.eryxis@gmail.com</p>"
                + "<p>Orari di supporto: Lun-Ven 9:00 - 9:01</p>"
                + "</body></html>";
        // Crea l'oggetto MimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // Crea un MimeMessageHelper per impostare correttamente l'email
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(email);
            messageHelper.setSubject("Il tuo codice OTP");
            messageHelper.setText(htmlContent, true); // true indica che il contenuto è HTML

            // Invia l'email
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // Gestisci eventuali errori
        }
    }

    public boolean validateOTP(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }
}