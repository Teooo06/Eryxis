package com.eryxis.eryxis.service.Security;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;

public class QRCodeService {

    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToFile(bitMatrix, "PNG", new File(filePath));
    }

    public static String getQRCodeURL(String user, String secret) {
        // Costruisci l'URL del codice QR
        return "otpauth://totp/MyApp:" + user + "?secret=" + secret + "&issuer=MyApp";
    }
}