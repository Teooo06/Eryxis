package com.eryxis.eryxis.service.Security;

import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class PasswordService {
    @Value("${AES.key}")
    private String secretKey;

    private SecretKeySpec keySpec;
    private static final String ALGORITHM = "AES";

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostConstruct
    private void init() {
        // Inizializza la chiave dopo l'iniezione
        keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
    }

    public String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }

    public String encrypt(String input) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return Base64.getUrlEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedInput) throws Exception {
        encryptedInput = encryptedInput.trim();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getUrlDecoder().decode(encryptedInput);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}
