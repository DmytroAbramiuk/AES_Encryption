package AES.Operations;

import AES.Constants;

public class AesDecryption {
    public byte[][] inverseSubBytes(byte[][] state) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                int row = (state[i][j] & 0xF0) >>> 4;
                int col = (state[i][j] & 0x0F);
                state[i][j] = (byte) Constants.INVERSE_S_BOX[row][col];
            }
        }
        return state;
    }

    public byte[][] inverseShiftRows(byte[][] state) {
        byte[][] res = {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        for (int step = 0; step < 4; step++) {
            System.arraycopy(state[step], state.length - step, res[step], 0, 4 - (state.length - step));
            System.arraycopy(state[step], 0, res[step], step, state.length - step);
        }
        return res;
    }

    public byte[][] inverseMixColumn(byte[][] state) {
        for (int i = 0; i < 4; i++) {
            byte[] col = new byte[4];
            for (int j = 0; j < 4; j++) {
                col[j] = state[j][i];
            }
            mixColumnCol(col);
            for (int j = 0; j < 4; j++) {
                state[j][i] = col[j];
            }
        }

        return state;
    }

    private byte[] mixColumnCol(byte[] col) {
        byte[] tmp = new byte[4];
        System.arraycopy(col, 0, tmp, 0, 4);

        col[0] = (byte) (multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[0][0], tmp[0])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[0][1], tmp[1])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[0][2], tmp[2])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[0][3], tmp[3]));
        col[1] = (byte) (multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[1][0], tmp[0])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[1][1], tmp[1])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[1][2], tmp[2])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[1][3], tmp[3]));
        col[2] = (byte) (multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[2][0], tmp[0])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[2][1], tmp[1])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[2][2], tmp[2])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[2][3], tmp[3]));
        col[3] = (byte) (multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[3][0], tmp[0])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[3][1], tmp[1])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[3][2], tmp[2])
                ^ multiply(Constants.INVERSE_MIX_COLUMN_MATRIX[3][3], tmp[3]));

        return col;
    }

    private byte multiply(int a, byte b) {
        int result = 0;
        while (a != 0) {
            if ((a & 1) != 0) {
                result ^= b;
            }
            boolean highBitSet = (b & 0x80) != 0;
            b <<= 1;
            if (highBitSet) {
                b ^= 0x1b; // XOR with the irreducible polynomial x^8 + x^4 + x^3 + x + 1
            }
            a >>>= 1;
        }
        return (byte) result;
    }
}
