package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Permessi;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.service.Security.OTPService;
import com.eryxis.eryxis.service.UtentiService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Controller
@RequiredArgsConstructor
public class RegisterController {

    @Autowired
    private UtentiService utentiService;

    @GetMapping("/register")
    public String registerPage(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("msg", msg);
        return "register";
    }

    /* Ci sono 3 pagine,
        nella prima (register) viene inserito nome, cognome, mail
        nella seconda(registerInfo) viene inserito datadinascita, indirizzo, codice fiscale, prefisso e telefono
        nella terza (registerPassword) viene inserita la password e la conferma della password
        nella quarta (registerOTP) viene chiesta la scelta per utilizzare l'otp di google o l'otp via mail
    */

    @PostMapping("/registerInfo")
    public String registerInfo(@ModelAttribute Utenti utente, HttpSession session, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
        utente.setNome(firstName);
        utente.setCognome(lastName);
        utente.setMail(email);
        session.setAttribute("utente", utente);
        return "registerInfo"; // pagina successiva
    }

    @PostMapping("/registerPassword")
    public String registerPassword(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDate,
                                   @RequestParam String address,
                                   @RequestParam String fiscalCode,
                                   @RequestParam String phonePrefix,
                                   @RequestParam String phoneNumber,
                                   HttpSession session) {
        Utenti utenti = (Utenti) session.getAttribute("utente");
        java.sql.Date date = new java.sql.Date(birthDate.getTime());
        utenti.setDataNascita(date);
        utenti.setIndirizzo(address);
        utenti.setCodiceFiscale(fiscalCode);
        utenti.setPrefisso(phonePrefix);
        utenti.setTelefono(phoneNumber);
        return "registerPassword";
    }

    @PostMapping("/registerOTP")
    public String registerOTP(@RequestParam String password,
                              @RequestParam String confirmPassword,
                              HttpSession session, Model model) throws Exception {
        if (!password.equals(confirmPassword)) {
            return "redirect:/registerPassword?msg=Le%20password%20non%20corrispondono";
        }
        Utenti utenti = (Utenti) session.getAttribute("utente");
        utenti.setPassword(password);
        // Genero l'OTP
        String otpSecret = OTPService.generateSecretKey();
        utenti.setPassPhrase(otpSecret); // salva nel DB o sessione
        String accountName = utenti.getMail(); // o nome utente
        String qrCodeURL = OTPService.getQRCodeURL(accountName, otpSecret);
        OTPService.generateQRCodeImage(qrCodeURL, 350, 350, "QRCode.png");
        model.addAttribute("secret", otpSecret);
        model.addAttribute("accountName", accountName);
        //OTPService.startOtpLoop(otpSecret); // per il debug
        return "registerOTP";
    }

    @GetMapping(value = "/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    public void generateQRCode(@RequestParam String secret, @RequestParam String user, HttpServletResponse response) throws IOException {
        String otpAuthURL = "otpauth://totp/EryxisBank:" + user + "?secret=" + secret + "&issuer=EryxisBank";

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 250, 250);
            OutputStream outputStream = response.getOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            outputStream.flush();
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/registerUser")
    public String registerUser(@RequestParam String otpMethod, HttpSession session) {
        Utenti utenti = (Utenti) session.getAttribute("utente");
        if (utenti == null) {
            return "redirect:/register?msg=Sessione%20scaduta";
        }
        if (utenti.getNome() == null || utenti.getCognome() == null || utenti.getMail() == null ||
                utenti.getDataNascita() == null || utenti.getIndirizzo() == null || utenti.getCodiceFiscale() == null ||
                utenti.getPrefisso() == null || utenti.getTelefono() == null) {
            return "redirect:/register?msg=Compila%20tutti%20i%20campi";
        }
        if (utenti.getPassword() == null || utenti.getPassword().isEmpty()) {
            return "redirect:/register?msg=Inserisci%20una%20password";
        }
        if (utenti.getPassPhrase() == null || utenti.getPassPhrase().isEmpty()) {
            return "redirect:/register?msg=Genera%20un%20codice%20OTP";
        }
        if (("mail").equals(otpMethod)) {
            utenti.setOTP(false);
        } else if (("authenticator").equals(otpMethod)) {
            utenti.setOTP(true);
        } else {
            return "redirect:/registerOTP?msg=Seleziona%20un%20metodo%20OTP";
        }

        if (utentiService.esisteByMail(utenti.getMail())) {
            return "redirect:/register?msg=Email%20già%20registrata";
        }


        utentiService.save(utenti);
        session.removeAttribute("utente");
        return "redirect:/login?msg=Registrazione%20completata";
    }
}
