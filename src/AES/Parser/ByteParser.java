package AES.Parser;

public class ByteParser {
    public static byte[][] getTextByteArray(String[] text) {
        byte[][] bytes = new byte[4][4];

        int hexIndex = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < bytes.length; j++) {
                bytes[j][i] = (byte) Short.parseShort(text[hexIndex], 16);
                hexIndex++;
            }
        }

        return bytes;
    }

    public static String getMessage(byte[][] textByteArray){
        StringBuilder sb = new StringBuilder();

        for(int i =0; i< textByteArray.length; i++){
            for(int j = 0; j<textByteArray.length; j++){
                sb.append((char) (textByteArray[j][i] & 0xFF));
            }
        }

        return sb.toString();
    }
}
