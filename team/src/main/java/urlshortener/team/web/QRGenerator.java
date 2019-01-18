/*
  Based on the original code extracted from https://www.callicoder.com/generate-qr-code-in-java-using-zxing/
 */
package urlshortener.team.web;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRGenerator {

    /*
     This method takes the text to be encoded, the width and height of the QR Code,
     and returns the QR Code in the form of a byte array.
    */
    public static byte[] getQRCodeImage(String text) throws WriterException, IOException {
        int width = 350;
        int height = 350;
        text = "http://locahost:8080/"+text;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }
}