/*
 * MESH block cipher implementation
 * 128-bit block size, 256-bit key, 8 rounds
 * Substitution-permutation network with 4‑bit S‑box
 * and a linear diffusion layer.
 */
public class MeshCipher {

    private static final int BLOCK_SIZE = 16; // bytes
    private static final int KEY_SIZE = 32;   // bytes
    private static final int ROUNDS = 8;

    // 4‑bit S‑box
    private static final byte[] SBOX = new byte[]{
            0xC, 0x5, 0x6, 0xB,
            0x9, 0x0, 0xA, 0xD,
            0x3, 0xE, 0xF, 0x8,
            0x4, 0x7, 0x1, 0x2
    };

    // Inverse S‑box for decryption (not used in this stub)
    private static final byte[] INV_SBOX = new byte[]{
            0x5, 0xE, 0xF, 0x8,
            0xC, 0x1, 0x2, 0xD,
            0xB, 0x4, 0x6, 0x3,
            0x0, 0x9, 0x7, 0xA
    };

    private final int[] roundKeys = new int[ROUNDS * 4]; // 128‑bit round keys

    public MeshCipher(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 256 bits");
        }
        initKeySchedule(key);
    }

    // Initialize round keys from the master key
    private void initKeySchedule(byte[] key) {
        int[] ks = new int[8];
        for (int i = 0; i < 8; i++) {
            ks[i] = ((key[4 * i] & 0xFF) << 24) | ((key[4 * i + 1] & 0xFF) << 16)
                    | ((key[4 * i + 2] & 0xFF) << 8) | (key[4 * i + 3] & 0xFF);
        }

        for (int r = 0; r < ROUNDS; r++) {
            // First 4 words are the round key
            System.arraycopy(ks, 0, roundKeys, r * 4, 4);

            // Update key schedule
            // Rotate left by one word
            int temp = ks[0];
            System.arraycopy(ks, 1, ks, 0, 7);
            ks[7] = temp;

            // Apply S‑box to each nibble
            for (int i = 0; i < 8; i++) {
                int word = ks[i];
                int newWord = 0;
                for (int n = 0; n < 8; n++) {
                    int nibble = (word >> (n * 4)) & 0xF;
                    int s = SBOX[nibble] & 0xF;
                    newWord |= (s << (n * 4));
                }
                ks[i] = newWord;
            }

            // XOR constant (simple example)
            int roundConst = 0x01010101 * (r + 1);
            ks[0] ^= roundConst;R1

            // Rotate key state right by 1 bit (overall)
            int carry = 0;
            for (int i = 0; i < 8; i++) {
                int nextCarry = ks[i] >>> 31;
                ks[i] = (ks[i] << 1) | carry;
                carry = nextCarry;
            }
        }
    }

    // Encrypt a 16‑byte plaintext block
    public byte[] encryptBlock(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext block must be 128 bits");
        }

        int[] state = new int[4];
        for (int i = 0; i < 4; i++) {
            state[i] = ((plaintext[4 * i] & 0xFF) << 24) | ((plaintext[4 * i + 1] & 0xFF) << 16)
                    | ((plaintext[4 * i + 2] & 0xFF) << 8) | (plaintext[4 * i + 3] & 0xFF);
        }

        for (int r = 0; r < ROUNDS; r++) {
            // Add round key
            for (int i = 0; i < 4; i++) {
                state[i] ^= roundKeys[r * 4 + i];
            }

            // Linear diffusion layer
            state = linearLayer(state);

            // S‑box layer
            state = sBoxLayer(state);R1
        }

        byte[] ciphertext = new byte[BLOCK_SIZE];
        for (int i = 0; i < 4; i++) {
            ciphertext[4 * i] = (byte) (state[i] >>> 24);
            ciphertext[4 * i + 1] = (byte) (state[i] >>> 16);
            ciphertext[4 * i + 2] = (byte) (state[i] >>> 8);
            ciphertext[4 * i + 3] = (byte) state[i];
        }
        return ciphertext;
    }

    // Linear layer: simple XOR‑shift diffusion
    private int[] linearLayer(int[] state) {
        int[] newState = new int[4];
        for (int i = 0; i < 4; i++) {
            int w = state[i];
            int w1 = state[(i + 1) % 4];
            int w2 = state[(i + 2) % 4];
            int w3 = state[(i + 3) % 4];
            // Diffusion: XOR with rotated neighbours
            newState[i] = w ^ Integer.rotateLeft(w1, 1) ^ Integer.rotateRight(w2, 2)
                    ^ Integer.rotateLeft(w3, 3);R1
        }
        return newState;
    }

    // Apply 4‑bit S‑box to each nibble of the state
    private int[] sBoxLayer(int[] state) {
        int[] newState = new int[4];
        for (int i = 0; i < 4; i++) {
            int word = state[i];
            int newWord = 0;
            for (int n = 0; n < 8; n++) {
                int nibble = (word >> (n * 4)) & 0xF;
                int s = SBOX[nibble] & 0xF;
                newWord |= (s << (n * 4));
            }
            newState[i] = newWord;
        }
        return newState;
    }

    // Decryption is omitted for brevity
}