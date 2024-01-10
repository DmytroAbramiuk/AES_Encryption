package AES.key;

import AES.Constants;

import java.security.SecureRandom;

public class KeyGenerator {
    public static String generate128bitRandomKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        StringBuilder hexStringBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b & 0xFF)).append(" ");
        }

        return hexStringBuilder.toString();
    }

    private static byte[] gFunction(byte[] word, int round) {
        byte[] result = {0, 0, 0, 0};
        System.arraycopy(word, 1, result, 0, word.length - 1);
        System.arraycopy(word, 0, result, word.length - 1, 1);

        for (int i = 0; i < result.length; i++) {
            int row = (result[i] & 0xF0) >>> 4;
            int col = (result[i] & 0x0F);
            result[i] = (byte) Constants.S_BOX[row][col];
        }

        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (result[i] ^ Constants.ROUND_CONSTANTS[round][i]);
        }

        return result;
    }

    public static byte[][] generateRoundKey(byte[][] currentKey, int round) {
        final int lastKeyWordIndex = 3;
        byte[] lastKeyWord = new byte[4];
        for (int word = lastKeyWordIndex; word < currentKey[0].length; word++) {
            for (int i = 0; i < lastKeyWord.length; i++) {
                lastKeyWord[i] = currentKey[i][word];
            }
        }
        lastKeyWord = gFunction(lastKeyWord, round);

        byte[] currentKeyWord = new byte[4];
        for (int word = 0; word < currentKey[0].length; word++) {
            for (int i = 0; i < currentKeyWord.length; i++) {
                currentKeyWord[i] = currentKey[i][word];
            }

            for (int j = 0; j < currentKeyWord.length; j++) {
                currentKey[j][word] = (byte) (currentKeyWord[j] ^ lastKeyWord[j]);
                lastKeyWord[j] = currentKey[j][word];
            }
        }


        return currentKey;
    }
}
