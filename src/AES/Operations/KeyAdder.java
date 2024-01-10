package AES.Operations;

public class KeyAdder {
    public static byte[][] addRoundKey(byte[][] state, byte[][] roundKey) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                state[j][i] = (byte) (state[j][i] ^ roundKey[j][i]);
            }
        }
        return state;
    }
}
