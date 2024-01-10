package AES.Parser;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class TextRetyper {

    private static String addMissingBits(String text) {
        int byteDifference = 0;

        byteDifference = 16 - text.length();
        StringBuilder textBuilder = new StringBuilder(text);
        for (int i = 0; i < byteDifference; i++) {
            textBuilder.append(0x00);
        }

        return textBuilder.toString();
    }

    public static String encryptTextToHex(String text) {
        byte[] byteText;

        if (text.length() < 16) {
            text = addMissingBits(text);
        }

        byteText = text.getBytes();

        StringBuilder hexStringBuilder = new StringBuilder(2 * byteText.length);
        for (byte b : byteText) {
            hexStringBuilder.append(String.format("%020x", b & 0xFF)).append(" ");
        }

        return hexStringBuilder.toString();
    }

    public static String decryptTextToHex(String text) {
        String hex = String.format("%020x", new BigInteger(1, text.getBytes(StandardCharsets.ISO_8859_1)));

        StringBuilder sb = new StringBuilder();
        int counter = 1;
        for (char ch : hex.toCharArray()) {
            sb.append(ch);
            if (counter % 2 == 0) {
                sb.append(" ");
            }
            counter++;
        }

        return sb.toString();
    }
}
