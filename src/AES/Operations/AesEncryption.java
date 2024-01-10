package AES.Operations;

import AES.Constants;

public class AesEncryption {
    public byte[][] subBytes(byte[][] state) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                int row = (state[j][i] & 0xF0) >>> 4;
                int col = (state[j][i] & 0x0F);
                state[j][i] = (byte) Constants.S_BOX[row][col];
            }
        }
        return state;
    }

    public byte[][] shiftRows(byte[][] state) {
        byte[][] res = {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        for (int step = 0; step < 4; step++) {
            System.arraycopy(state[step], step, res[step], 0, state.length - step);
            System.arraycopy(state[step], 0, res[step], state.length - step, step);
        }
        return res;
    }

    public byte[][] mixColumns(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            state = mixSingleColumn(state, i);
        }
        return state;
    }

    private static byte[][] mixSingleColumn(byte[][] state, int col) {

        byte[] column = new byte[4];
        // Copy the column into a separate array
        for (int i = 0; i < 4; i++) {
            column[i] = state[i][col];
        }

        // Perform the MixColumns operation on the column
        state[0][col] = (byte) (galoisMultiply(column[0], Constants.FIXED_POLYNOMIAL[0]) ^ galoisMultiply(column[1], Constants.FIXED_POLYNOMIAL[1]) ^ column[2] ^ column[3]);
        state[1][col] = (byte) (column[0] ^ galoisMultiply(column[1], Constants.FIXED_POLYNOMIAL[0]) ^ galoisMultiply(column[2], Constants.FIXED_POLYNOMIAL[1]) ^ column[3]);
        state[2][col] = (byte) (column[0] ^ column[1] ^ galoisMultiply(column[2], Constants.FIXED_POLYNOMIAL[0]) ^ galoisMultiply(column[3], Constants.FIXED_POLYNOMIAL[1]));
        state[3][col] = (byte) (galoisMultiply(column[0], Constants.FIXED_POLYNOMIAL[1]) ^ column[1] ^ column[2] ^ galoisMultiply(column[3], Constants.FIXED_POLYNOMIAL[0]));

        return state;
    }

    private static byte galoisMultiply(byte a, int b) {
        byte result = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) == 1) {
                result ^= a;
            }
            boolean highBitSet = (a & 0x80) != 0;
            a <<= 1;
            if (highBitSet) {
                a ^= 0x1B; // XOR with the irreducible polynomial x^8 + x^4 + x^3 + x + 1
            }
            b >>= 1;
        }
        return result;
    }
}
