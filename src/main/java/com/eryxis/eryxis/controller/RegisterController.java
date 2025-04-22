package com.eryxis.eryxis.controller;

import com.eryxis.eryxis.model.Carte;
import com.eryxis.eryxis.model.Conti;
import com.eryxis.eryxis.model.Permessi;
import com.eryxis.eryxis.model.Utenti;
import com.eryxis.eryxis.repository.CarteRepository;
import com.eryxis.eryxis.repository.ContiRepository;
import com.eryxis.eryxis.service.CarteService;
import com.eryxis.eryxis.service.ContiService;
import com.eryxis.eryxis.service.PermessiService;
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
    @Autowired
    private PermessiService permessiService;
    @Autowired
    private ContiService contiService;
    @Autowired
    private CarteService carteService;
    @Autowired
    private CarteRepository carteRepository;

    @GetMapping("/register")
    public String registerPage(Model model, @RequestParam(required = false) String msg) {
        model.addAttribute("msg", msg);
        return "register";
    }

    /* Ci sono 3 pagine,
        nella prima (register) viene inserito nome, cognome, mail
        nella seconda(registerInfo) viene inserito datadinascita,toponimo, indirizzo, codice fiscale, prefisso e telefono
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
                                      @RequestParam String toponimo,
                                      @RequestParam int number,
                                   @RequestParam String fiscalCode,
                                   @RequestParam String phonePrefix,
                                   @RequestParam String phoneNumber,
                                   HttpSession session) {
        Utenti utenti = (Utenti) session.getAttribute("utente");
        java.sql.Date date = new java.sql.Date(birthDate.getTime());
        utenti.setDataNascita(date);
        utenti.setIndirizzo(address);
        utenti.setToponimo(toponimo);
        utenti.setNumeroCivico(number);
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
        if (("email").equals(otpMethod)) { // Il metodo OTP via mail OTP = 0
            utenti.setOTP(false);
        } else if (("authenticator").equals(otpMethod)) {
            utenti.setOTP(true); // Il metodo OTP via Google Authenticator OTP = 1
        } else {
            return "redirect:/registerOTP?msg=Seleziona%20un%20metodo%20OTP";
        }

        if (utentiService.esisteByMail(utenti.getMail())) {
            return "redirect:/register?msg=Email%20già%20registrata";
        }

        // Aggiungi permessi di default
        Permessi permesso = permessiService.findByPermesso(3);
        if (permesso == null) {
            return "redirect:/register?msg=Errore%20nel%20recupero%20dei%20permessi";
        }
        utenti.setPermesso(permesso);

        utentiService.save(utenti);

        // Creo un conto e una carta di credito

        Conti conto = new Conti();
        conto.setUtente(utenti);
        conto.setValuta("EUR");
        conto.setSaldo(0);
        // Chiamo una funzione per generare un IBAN casuale
        String iban = creaIBAN();
        conto.setIBAN(iban);
        Utenti consulente = utentiService.findByIdUtente(1);
        conto.setConsulente(consulente);
        conto.setSWIFT("ERXSITMM");
        contiService.save(conto);

        // Creo una carta di credito
        Carte carte = cartaNuova(conto);
        carteRepository.save(carte);

        session.removeAttribute("utente");
        return "redirect:/login?msg=Registrazione%20completata";
    }

    private Carte cartaNuova(Conti conto) {
        Carte carte = new Carte();
        carte.setConto(conto);
        carte.setDataScadenza(new java.sql.Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L)); // Scadenza tra un anno
        // Creo un numero di carta casuale lungo 16 cifre
        StringBuilder numeroCarta = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
            numeroCarta.append(randomDigit);
        }
        //Controllo che il numero di carta non esista già
        while (carteService.findByNumeroCarta(numeroCarta.toString()) != null) {
            numeroCarta.setLength(0); // Pulisci il StringBuilder
            for (int i = 0; i < 16; i++) {
                int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
                numeroCarta.append(randomDigit);
            }
        }
        carte.setNumeroCarta(numeroCarta.toString());
        // Creo un codice CVV casuale lungo 3 cifre
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
            cvv.append(randomDigit);
        }
        //Controllo che il CVV non esista già
        while (carteService.findByCVV(cvv.toString()) != null) {
            cvv.setLength(0); // Pulisci il StringBuilder
            for (int i = 0; i < 3; i++) {
                int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
                cvv.append(randomDigit);
            }
        }
        carte.setCVV(cvv.toString());
        // Imposto il pin
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
            pin.append(randomDigit);
        }
        // Controllo che il pin non esista già
        while (carteService.findByPIN(pin.toString()) != null) {
            pin.setLength(0); // Pulisci il StringBuilder
            for (int i = 0; i < 5; i++) {
                int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
                pin.append(randomDigit);
            }
        }
        carte.setPIN(pin.toString());

        carte.setTipo("Credito");
        carte.setSaldoContabile(0);
        carte.setSaldoDisponibile(0);

        return carte;
    }

    public String creaIBAN(){
        String IBAN = "IT60XIT60X0542811101";
        // Genera una stringa di 7 caratteri numerici
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
            sb.append(randomDigit);
        }
        // Aggiungi la stringa generata all'IBAN
        IBAN += sb.toString();
        // Controllo che l'IBAN non esista già
        while (contiService.findByIBAN(IBAN) != null) {
            // Se l'IBAN esiste già, genera un nuovo IBAN
            sb.setLength(0); // Pulisci il StringBuilder
            for (int i = 0; i < 7; i++) {
                int randomDigit = (int) (Math.random() * 10); // Genera un numero casuale tra 0 e 9
                sb.append(randomDigit);
            }
            // Aggiungi la stringa generata all'IBAN
            IBAN = "IT60XIT60X0542811101" + sb.toString();
        }
        return IBAN;
    }
}
