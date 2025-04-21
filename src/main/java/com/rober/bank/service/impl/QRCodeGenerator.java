package com.rober.bank.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Hashtable;

@Service
public class QRCodeGenerator {
    // Metodo para generar los datos que se codificarán en el QR
    public String generateQRCodeData(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        return "sourceAccountNumber=" + sourceAccountNumber
                + "&destinationAccountNumber=" + destinationAccountNumber
                + "&amount=" + amount.toString();
    }

    // Metodo para generar el QR como un BitMatrix
    public BitMatrix generateQRCode(String data) throws Exception {
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        MultiFormatWriter writer = new MultiFormatWriter();
        return writer.encode(data, BarcodeFormat.QR_CODE, 200, 200, hints);
    }

    // Metodo para convertir el BitMatrix en una imagen BufferedImage
    public BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        return image;
    }

    // Metodo para agregar un logo al centro del QR
    private void addLogo(BufferedImage qrImage, String logoPath) throws IOException {
        // Cargar el logo
        BufferedImage logo = ImageIO.read(new File(logoPath));

        // Calcular el tamaño y la posición del logo
        int width = qrImage.getWidth();
        int height = qrImage.getHeight();
        int logoSize = width / 5;  // El logo ocupará el 1/5 del tamaño del QR

        int x = (width - logoSize) / 2;
        int y = (height - logoSize) / 2;

        // Redimensionar el logo
        Image resizedLogo = logo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);

        // Dibujar el logo en el centro del QR
        Graphics2D graphics = qrImage.createGraphics();
        graphics.drawImage(resizedLogo, x, y, null);
        graphics.dispose();
    }

    // Metodo para generar el QR con logo
    public byte[] generateQRCodeWithLogo(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount, String logoPath) throws Exception {

        String qrData = generateQRCodeData(sourceAccountNumber, destinationAccountNumber, amount);


        BitMatrix matrix = generateQRCode(qrData);

        // Convertir el BitMatrix a una imagen
        BufferedImage qrImage = toBufferedImage(matrix);


        addLogo(qrImage, logoPath);

        // Convertir la imagen en un byte array para la respuesta HTTP
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
