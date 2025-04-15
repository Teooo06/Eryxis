package com.eryxis.eryxis.service.Security;
import com.eryxis.eryxis.service.UtentiService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import de.taimos.totp.TOTP;


@Service
public class OTPService {
    private final JavaMailSender mailSender;
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    private UtentiService utentiService;

    // istanza per l'OTP service
    //private GoogleAuthenticator googleAuthenticator;

    public OTPService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        //this.googleAuthenticator = new GoogleAuthenticator();
    }

    public String generateOTP(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 cifre
        otpStorage.put(email, otp);
        sendOTPEmail(email, otp);
        return otp;
    }

    private void sendOTPEmail(String email, String otp) {
        String htmlContent = "<html><body>"
                + "<h2 style=\"margin: 0; padding-bottom: 10px; color: #FFD709; text-align: center;\">Eryxsis Bank</h2>"
                + "<hr style=\"border: none; height: 2px; background-color: #ccc; margin: 10px 0;\">"
                + "<h3>Hey USER,</h3>"
                + "<p>Qualcuno che conosce la password ha provato ad accedere al tuo account</p>"
                + "<h4>Se sei stato tu, ecco il codice di verifica:</h4>"
                + "<div style=\"width: 300px; margin: 0 auto; background-color: #f0f0f0; border-radius: 10px; padding: 20px; text-align: center;\">"
                + "<p>Il tuo codice OTP è: <strong>" + otp + "</strong></p>"
                + "</div>"
                + "<h5>Se non sei tu ad aver provato l'accesso, per favore, ignora questa Mail</h5>"
                + "<hr style=\"border: none; height: 2px; background-color: #ccc; margin: 10px 0;\">"
                + "<div style=\"width: 100%; margin: 0px; background-color: #f8f8f8; border-radius: 10px; padding: 20px; text-align: center; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2>Eryxis Bank</h2>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #333;\">\uD83D\uDCB3 Soluzioni Bancarie Sicure e Moderne</p>"
                + "<hr style=\"border: none; height: 2px; background-color: #FFD700; margin: 10px auto; width: 80%;\">"
                + "<p style=\"font-size: 16px; color: #555;\">\uD83D\uDCE7 <a href=\"mailto:info.eryxis@gmail.com\" style=\"color: #0073e6; text-decoration: none;\">info.eryxis@gmail.com</a></p>"
                + "<p style=\"font-size: 14px; color: #777;\">\uD83D\uDD52 Orari di supporto: <strong>Lun-Ven 9:00 - 9:01</strong></p>"
                + "</div>"
                +"                </body></html>";
        // Crea l'oggetto MimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        //codeOTP();

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


    /* Metodi per l'utilizzo dell'OTP con Google Authenticator
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }
     */

    public String generateSecretKey() {
        String secretKey = generateSecretKey();
        return secretKey;
    }


    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public boolean validateOTPGoogle(String mail, String codice) {
        return codice.equals(getTOTPCode(utentiService.findPassPhrase(mail)));
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}