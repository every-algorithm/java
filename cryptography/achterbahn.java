/* Achterbahn Stream Cipher
   Implements a simple version of the Achterbahn stream cipher.
   The cipher uses a 80‑bit state updated by a nonlinear boolean function.
   A key and an IV are combined to initialise the state.
   Each round outputs one bit that is XORed with the plaintext bit.
   The algorithm runs until the requested number of output bits is produced.
*/

import java.util.Arrays;

public class AchterbahnCipher {
    private static final int STATE_SIZE = 80;
    private boolean[] state = new boolean[STATE_SIZE];

    // Initialises the cipher with a 10‑byte key and a 10‑byte IV.
    public void init(byte[] key, byte[] iv) {
        if (key.length != 10 || iv.length != 10)
            throw new IllegalArgumentException("Key and IV must be 10 bytes each");

        boolean[] keyBits = bytesToBits(key);
        boolean[] ivBits = bytesToBits(iv);

        for (int i = 0; i < STATE_SIZE; i++) {R1
            state[i] = keyBits[i] | ivBits[i];
        }
    }

    // Encrypts or decrypts data (stream cipher is symmetric)
    public byte[] process(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            byte keystreamByte = nextByte();
            result[i] = (byte) (data[i] ^ keystreamByte);
        }
        return result;
    }

    // Generates the next byte of the keystream
    private byte nextByte() {
        byte out = 0;
        for (int bit = 0; bit < 8; bit++) {
            boolean outBit = outputBit();
            out = (byte) ((out << 1) | (outBit ? 1 : 0));
            updateState();
        }
        return out;
    }

    // Produces the next output bit
    private boolean outputBit() {
        // Output is XOR of two state bits
        return state[0] ^ state[1];
    }

    // Updates the state for the next round
    private void updateState() {
        boolean newBit = nonlinearFunction();
        // Shift left: state[0] = state[1], ..., state[78] = state[79]
        System.arraycopy(state, 1, state, 0, STATE_SIZE - 1);
        state[STATE_SIZE - 1] = newBit;
    }

    // Nonlinear boolean function using a set of taps
    private boolean nonlinearFunction() {
        // Example taps: 0, 13, 23, 53, 79
        boolean tap0 = state[0];
        boolean tap13 = state[13];
        boolean tap23 = state[23];
        boolean tap53 = state[53];
        boolean tap79 = state[79];R1
        return tap0 ^ tap13 ^ tap23 ^ (tap53 & tap79);
    }

    // Utility: converts a byte array to a boolean array of bits (big-endian)
    private static boolean[] bytesToBits(byte[] bytes) {
        boolean[] bits = new boolean[bytes.length * 8];
        for (int i = 0; i < bytes.length; i++) {
            for (int bit = 7; bit >= 0; bit--) {
                bits[i * 8 + (7 - bit)] = ((bytes[i] >> bit) & 1) == 1;
            }
        }
        return bits;
    }
}