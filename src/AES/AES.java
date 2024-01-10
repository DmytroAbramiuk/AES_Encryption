package AES;

import AES.Operations.AesDecryption;
import AES.Operations.AesEncryption;
import AES.Operations.KeyAdder;
import AES.Parser.ByteParser;
import AES.Parser.TextRetyper;
import AES.key.KeyGenerator;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AES {
    private static final int ROUND_NUMBER = 10;
    private static final int LAST_ROUND = 10;
    private static final int FIRST_ROUND = 1;
    private static final AesEncryption encryption = new AesEncryption();
    private static final AesDecryption decryption = new AesDecryption();

    public static String encrypt(String plainText, String key) {
        byte[] paddedText = padBytes(plainText.getBytes(StandardCharsets.UTF_8));
        String hexPlainText = TextRetyper.encryptTextToHex(new String(paddedText));

        byte[][] state = ByteParser.getTextByteArray(hexPlainText.split(" "));
        byte[][] roundKey = ByteParser.getTextByteArray(key.split(" "));

        KeyAdder.addRoundKey(state, roundKey);
        for (int round = FIRST_ROUND; round <= ROUND_NUMBER; round++) {
            state = encryption.subBytes(state);
            state = encryption.shiftRows(state);
            if (round != LAST_ROUND) {
                state = encryption.mixColumns(state);
            }
            roundKey = KeyGenerator.generateRoundKey(roundKey, round);
            state = KeyAdder.addRoundKey(state, roundKey);
        }

        return ByteParser.getMessage(state);
    }

    private static byte[][] copyKey(byte[][] initialKey) {
        int rows = initialKey.length;

        byte[][] targetArray = new byte[rows][];
        for (int i = 0; i < rows; i++) {
            targetArray[i] = Arrays.copyOf(initialKey[i], initialKey[i].length);
        }

        return targetArray;
    }

    private static byte[][] getACurrentRoundKey(byte[][] initialKey, int round) {
        byte[][] roundKey = copyKey(initialKey);

        for (int i = 1; i <= round; i++) {
            roundKey = KeyGenerator.generateRoundKey(roundKey, i);
        }
        return roundKey;
    }

    public static String decrypt(String cipherText, String key) {
        String hexPlainText = TextRetyper.decryptTextToHex(cipherText);
        byte[][] state = ByteParser.getTextByteArray(hexPlainText.split(" "));

        byte[][] initialKey = ByteParser.getTextByteArray(key.split(" "));
        byte[][] roundKey;

        roundKey = getACurrentRoundKey(initialKey, LAST_ROUND);
        KeyAdder.addRoundKey(state, roundKey);

        for (int round = ROUND_NUMBER; round >= FIRST_ROUND; round--) {
            state = decryption.inverseShiftRows(state);
            state = decryption.inverseSubBytes(state);
            if (round == FIRST_ROUND) {
                state = KeyAdder.addRoundKey(state, initialKey);
            } else {
                roundKey = getACurrentRoundKey(initialKey, round - 1);
                state = KeyAdder.addRoundKey(state, roundKey);
            }
            if (round != FIRST_ROUND) {
                state = decryption.inverseMixColumn(state);
            }
        }

        return new String(unPadBytes(ByteParser.getMessage(state).getBytes(StandardCharsets.UTF_8)));
    }

    private static byte[] padBytes(byte[] input) {
        int paddingLength = 16 - (input.length % 16);
        if(paddingLength == 16){
            return input;
        }
        else {

            byte[] padded = new byte[input.length + paddingLength];
            System.arraycopy(input, 0, padded, 0, input.length);
            Arrays.fill(padded, input.length, padded.length, (byte) paddingLength);
            return padded;
        }

    }

    private static byte[] unPadBytes(byte[] paddedBytes) {
        int paddingLength = paddedBytes[paddedBytes.length - 1];
        if(paddingLength < 16 && paddingLength > 1) {
            int unPaddedSize = paddedBytes.length - paddingLength;
            byte[] unPaddedBytes = new byte[unPaddedSize];
            System.arraycopy(paddedBytes, 0, unPaddedBytes, 0, unPaddedSize);
            return unPaddedBytes;
        }
        else {
            return paddedBytes;
        }

    }
}
