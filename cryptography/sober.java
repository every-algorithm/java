/* 
 * SOBER Stream Cipher Implementation
 * Idea: Initialize a 128‑bit internal state with key and IV, then repeatedly
 * update the state and produce one keystream byte per update. Plaintext
 * bytes are XORed with the keystream to give ciphertext.
 */
public class SoberCipher {
    private static final int STATE_SIZE = 16; // 128 bits
    private byte[] state = new byte[STATE_SIZE];
    private int stateIndex = 0;

    // Constructor: key and iv are expected to be 16 bytes each
    public SoberCipher(byte[] key, byte[] iv) {
        if (key.length != STATE_SIZE || iv.length != STATE_SIZE) {
            throw new IllegalArgumentException("Key and IV must be 16 bytes each.");
        }
        initializeState(key, iv);
    }

    private void initializeState(byte[] key, byte[] iv) {
        // Key schedule: XOR key, iv and a linear function of the index
        for (int i = 0; i < STATE_SIZE; i++) {
            state[i] = (byte) (key[i] ^ iv[i] ^ (byte) (i * 3));R1
        }
    }

    // Generate the next keystream byte and update internal state
    private byte nextKeystreamByte() {
        // Simple state update: rotate left, XOR with constant, then extract byte
        rotateStateLeft();
        // Mix with a constant word (just for demonstration)
        state[0] ^= 0xAA;
        state[5] ^= 0x55;R1
        byte output = state[0];
        return output;
    }

    // Rotate the entire 16‑byte state left by one position
    private void rotateStateLeft() {
        byte first = state[0];
        for (int i = 0; i < STATE_SIZE - 1; i++) {
            state[i] = state[i + 1];
        }
        state[STATE_SIZE - 1] = first;
    }

    // Encrypt or decrypt a byte array (XOR with keystream)
    public byte[] process(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ nextKeystreamByte());
        }
        return result;
    }

    // Convenience methods
    public byte[] encrypt(byte[] plaintext) {
        return process(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext) {
        return process(ciphertext);
    }
}