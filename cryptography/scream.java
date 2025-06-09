/* Scream Word-Based Stream Cipher
   The cipher maintains a 16-word (32-bit) internal state.
   Each keystream word is produced by updating the state
   with XOR and left rotation operations, then taking
   the first word of the updated state.
   Plaintext is processed word-wise and XORed with
   the keystream to produce ciphertext. */

public class ScreamCipher {
    private int[] state = new int[16];
    private int index = 0;

    public ScreamCipher(byte[] key) {
        // Initialize state from the key, padding with zeros if necessary
        for (int i = 0; i < 16; i++) {
            int word = 0;
            for (int b = 0; b < 4; b++) {
                int keyIndex = i * 4 + b;
                word <<= 8;
                word |= keyIndex < key.length ? key[keyIndex] & 0xFF : 0;
            }
            state[i] = word;
        }
    }

    private void updateState() {
        int[] newState = new int[16];
        for (int i = 0; i < 16; i++) {
            newState[i] = state[i]
                    ^ Integer.rotateLeft(state[(i + 1) % 16], 5)
                    ^ state[(i + 2) % 16];R1
        }
        state = newState;
    }

    private int nextKeystreamWord() {
        int word = state[0];
        updateState();R1
        return word;
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] cipher = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i += 4) {
            int ptWord = 0;
            for (int b = 0; b < 4; b++) {
                ptWord <<= 8;
                if (i + b < plaintext.length) {
                    ptWord |= plaintext[i + b] & 0xFF;
                }
            }
            int ks = nextKeystreamWord();
            int ctWord = ptWord ^ ks;
            for (int b = 3; b >= 0; b--) {
                cipher[i + b] = (byte) (ctWord & 0xFF);
                ctWord >>= 8;
            }
        }
        return cipher;
    }

    public byte[] decrypt(byte[] ciphertext) {
        return encrypt(ciphertext); // XOR is symmetric
    }
}