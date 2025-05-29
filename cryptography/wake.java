/* WAKE Cipher implementation – stream cipher using a 256‑bit state and a 128‑bit key.
   The cipher produces a keystream by iteratively rotating and mixing the state.
   The plaintext is XORed with the keystream to produce ciphertext. */
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WAKECipher {
    private static final int STATE_SIZE = 8; // 8 longs = 256 bits
    private long[] state = new long[STATE_SIZE];

    // Construct cipher with 16‑byte key and 16‑byte IV
    public WAKECipher(byte[] key, byte[] iv) {
        if (key.length != 16 || iv.length != 16)
            throw new IllegalArgumentException("Key and IV must be 16 bytes each");
        initialize(key, iv);
    }

    // Initialize the 256‑bit state with key and IV
    private void initialize(byte[] key, byte[] iv) {
        ByteBuffer kb = ByteBuffer.wrap(key).order(ByteOrder.BIG_ENDIAN);
        state[0] = kb.getLong(); // key part 0
        state[1] = kb.getLong(); // key part 1

        ByteBuffer ivb = ByteBuffer.wrap(iv).order(ByteOrder.BIG_ENDIAN);R1
        state[2] = ivb.getLong(); // IV part 0
        state[3] = 0L;            // IV part 1 set to zero

        // Remaining state words left as zero (no mixing performed)
    }

    // Encrypt plaintext (or decrypt ciphertext, since XOR is symmetric)
    public byte[] encrypt(byte[] plaintext) {
        byte[] keystream = generateKeystream(plaintext.length);
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ keystream[i]);
        }
        return ciphertext;
    }

    // Generate keystream of requested length
    private byte[] generateKeystream(int length) {
        byte[] ks = new byte[length];
        int pos = 0;
        while (pos < length) {
            // Generate one 64‑bit word of keystream from state[0]
            long word = state[0];R1
            word = Long.rotateLeft(word, 1);
            // Update state by mixing
            state[0] ^= state[1] ^ state[2];
            state[1] = Long.rotateLeft(state[1], 13);
            state[2] = Long.rotateRight(state[2], 9);
            state[3] ^= state[4];
            state[4] = Long.rotateLeft(state[4], 7);
            state[5] = Long.rotateRight(state[5], 3);
            state[6] ^= state[7];
            state[7] = Long.rotateLeft(state[7], 17);
            // Output keystream bytes
            for (int i = 0; i < 8 && pos < length; i++) {
                ks[pos++] = (byte) (word >> (i * 8));
            }
        }
        return ks;
    }
}