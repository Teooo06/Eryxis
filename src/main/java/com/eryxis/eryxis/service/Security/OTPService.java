package com.eryxis.eryxis.service.Security;
import com.eryxis.eryxis.service.UtentiService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
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


    public OTPService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        //this.googleAuthenticator = new GoogleAuthenticator();
    }

    /**
     * Genera un codice OTP (One-Time Password) basato sul segreto specificato usando l'algoritmo TOTP.
     *
     * @param secret Il segreto in formato Base32.
     * @return Il codice OTP generato.
     * @throws IllegalArgumentException Se il segreto non è valido in Base32.
     */
    public static String generateOTP(String secret) {
        if (!isValidBase32(secret)) {
            throw new IllegalArgumentException("Segreto OTP non valido: " + secret);
        }
        String otpCode = TOTP.getOTP(secret);
        System.out.println("Codice OTP generato: " + otpCode);  // Stampa nel terminale
        return otpCode;
    }

    /**
     * Verifica se una stringa è un Base32 valido (composto solo da caratteri A-Z e numeri 2-7).
     *
     * @param secret Il segreto da validare.
     * @return true se il segreto è valido, false altrimenti.
     */
    private static boolean isValidBase32(String secret) {
        // Una funzione di validazione base32 semplice
        return secret.matches("[A-Z2-7]*");
    }


    /**
     * Genera un codice OTP casuale a 6 cifre e lo associa all'email specificata.
     * Invia il codice OTP all'indirizzo email fornito.
     *
     * @param email L'indirizzo email del destinatario.
     * @return Il codice OTP generato.
     */
    public String generateOTPa(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 cifre
        otpStorage.put(email, otp);
        sendOTPEmail(email, otp);
        return otp;
    }

    /**
     * Invia un'email contenente il codice OTP generato all'utente.
     *
     * @param email L'indirizzo email del destinatario.
     * @param otp Il codice OTP da inviare.
     */
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

    /**
     * Verifica se il codice OTP fornito corrisponde a quello associato all'email specificata.
     *
     * @param email L'indirizzo email dell'utente.
     * @param otp Il codice OTP da validare.
     * @return true se il codice è valido, false altrimenti.
     */
    public boolean validateOTP(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }


    /**
     * Genera una chiave segreta in formato Base32 per Google Authenticator.
     *
     * @return La chiave segreta generata.
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * Genera un'immagine QR code a partire dal testo specificato e la salva nel percorso indicato.
     *
     * @param text Il testo da codificare nel QR.
     * @param width Larghezza dell'immagine.
     * @param height Altezza dell'immagine.
     * @param filePath Percorso in cui salvare il file PNG del QR code.
     * @throws Exception Se avviene un errore nella generazione o nel salvataggio dell'immagine.
     */
    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToFile(bitMatrix, "PNG", new File(filePath));
    }

    /**
     * Restituisce l'URL per generare il QR code compatibile con Google Authenticator.
     *
     * @param user Il nome utente.
     * @param secret Il segreto OTP.
     * @return L'URL otpauth da usare per il QR code.
     */
    public static String getQRCodeURL(String user, String secret) {
        // Costruisci l'URL del codice QR
        return "otpauth://totp/MyApp:" + user + "?secret=" + secret + "&issuer=MyApp";
    }


    /**
     * Calcola un codice OTP TOTP (Time-based One-Time Password) a partire da una chiave segreta.
     *
     * @param secretKey La chiave segreta in Base32.
     * @return Il codice OTP generato.
     */
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    /**
     * Valida un codice OTP generato da Google Authenticator confrontandolo con quello atteso per l'utente.
     *
     * @param mail L'indirizzo email dell'utente.
     * @param codice Il codice OTP da validare.
     * @return true se il codice è valido, false altrimenti.
     */
    public boolean validateOTPGoogle(String mail, String codice) {
        startOtpLoop(utentiService.findPassPhrase(mail));
        return codice.equals(getTOTPCode(utentiService.findPassPhrase(mail)));
    }


    /**
     * Avvia un thread che stampa continuamente il codice OTP aggiornato ogni secondo.
     * Utile per il debugging.
     *
     * @param secret Il segreto OTP da usare per generare i codici.
     */
    public static void startOtpLoop(String secret) {
        Runnable otpLoop = new Runnable() {
            @Override
            public void run() {
                String lastCode = null;
                System.out.println(secret);
                while (true) {
                    String code = getTOTPCode(secret);
                    if (!code.equals(lastCode)) {
                        System.out.println(code);
                    }
                    lastCode = code;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    };
                }
            }
        };
        new Thread(otpLoop).start();  // Avvia il thread
    }


}