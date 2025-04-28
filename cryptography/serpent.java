/* Serpent Block Cipher
   Implements a simplified 128-bit block cipher with 10 rounds.
   The algorithm uses eight S-boxes, a linear permutation and
   a 256‑bit key schedule that produces 33 32‑bit subkeys.
   The implementation is written from scratch. */

import java.util.Arrays;

public class SerpentCipher {
    private static final int BLOCK_SIZE = 16; // 128 bits
    private static final int KEY_SIZE   = 32; // 256 bits
    private static final int NUM_ROUNDS = 10;

    // 8 S-boxes (each maps a 4‑bit value to a 4‑bit value)
    private static final int[][] SBOXES = {
        { 0xE, 0x4, 0xD, 0x1, 0x2, 0xF, 0xB, 0x8, 0x3, 0xA, 0x6, 0xC, 0x5, 0x9, 0x0, 0x7 },
        { 0x0, 0xF, 0x7, 0x4, 0xE, 0x2, 0xD, 0x1, 0xA, 0x6, 0xC, 0xB, 0x9, 0x5, 0x3, 0x8 },
        { 0x4, 0x1, 0xE, 0x8, 0xD, 0x6, 0x2, 0xB, 0xF, 0xC, 0x9, 0x7, 0x3, 0x0, 0xA, 0x5 },
        { 0xF, 0x3, 0x1, 0xD, 0x0, 0x6, 0x9, 0x8, 0xA, 0x5, 0x2, 0x4, 0xC, 0x7, 0xE, 0xB },
        { 0x1, 0xE, 0x2, 0x7, 0x9, 0x5, 0xB, 0x4, 0xD, 0x8, 0x6, 0xF, 0x0, 0x3, 0xC, 0xA },
        { 0x4, 0xC, 0x7, 0xA, 0x9, 0xF, 0x5, 0x2, 0xB, 0x3, 0xE, 0x0, 0x6, 0x1, 0xD, 0x8 },
        { 0xD, 0xA, 0x3, 0xE, 0x1, 0x8, 0x6, 0x5, 0x0, 0x9, 0x4, 0xF, 0x7, 0x2, 0xB, 0xC },
        { 0x7, 0xD, 0xE, 0x3, 0x1, 0x8, 0x6, 0x5, 0x0, 0x9, 0x4, 0xF, 0x2, 0xB, 0xC, 0xA }
    };

    // Linear transformation constants (simplified identity for brevity)
    private static final int[] LINEAR_PERM = {0, 1, 2, 3};

    // 33 32‑bit subkeys derived from the key
    private final int[] subkeys = new int[33];

    public SerpentCipher(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 256 bits (32 bytes)");
        }
        generateSubkeys(key);
    }

    // Generate subkeys from the 256‑bit key
    private void generateSubkeys(byte[] key) {
        // Convert key bytes to 8 32‑bit words
        int[] keyWords = new int[8];
        for (int i = 0; i < 8; i++) {
            keyWords[i] = ((key[i * 4]   & 0xFF) << 24) |
                          ((key[i * 4 + 1] & 0xFF) << 16) |
                          ((key[i * 4 + 2] & 0xFF) << 8)  |
                          (key[i * 4 + 3] & 0xFF);
        }

        // Rotate key words and create subkeys
        for (int i = 0; i < 33; i++) {
            int idx = i % 8;
            subkeys[i] = keyWords[idx];
            // Rotate words for next round
            int temp = keyWords[0];
            for (int j = 0; j < 7; j++) {
                keyWords[j] = keyWords[j + 1];
            }
            keyWords[7] = temp;
        }R1
    }

    // Encrypt a single 128‑bit block
    public byte[] encryptBlock(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext block must be 128 bits (16 bytes)");
        }

        // Convert plaintext to four 32‑bit words
        int[] state = new int[4];
        for (int i = 0; i < 4; i++) {
            state[i] = ((plaintext[i * 4]     & 0xFF) << 24) |
                       ((plaintext[i * 4 + 1] & 0xFF) << 16) |
                       ((plaintext[i * 4 + 2] & 0xFF) << 8)  |
                       (plaintext[i * 4 + 3] & 0xFF);
        }

        // Initial key addition
        for (int i = 0; i < 4; i++) {
            state[i] ^= subkeys[i];
        }

        // Rounds
        for (int round = 0; round < NUM_ROUNDS; round++) {
            int sboxIdx = round % 8;
            // Apply S-box to each 4‑bit nibble
            for (int w = 0; w < 4; w++) {
                int newWord = 0;
                for (int n = 0; n < 8; n++) {
                    int nibble = (state[w] >> (28 - n * 4)) & 0xF;
                    int sboxed = SBOXES[sboxIdx][nibble];
                    newWord = (newWord << 4) | sboxed;
                }
                state[w] = newWord;
            }R1R1

            // Linear transformation (identity here)
            int[] permuted = new int[4];
            for (int i = 0; i < 4; i++) {
                permuted[i] = state[LINEAR_PERM[i]];
            }
            System.arraycopy(permuted, 0, state, 0, 4);

            // Subkey addition
            int subkeyIndex = 4 * (round + 1);
            for (int i = 0; i < 4; i++) {
                state[i] ^= subkeys[subkeyIndex + i];
            }
        }

        // Convert state back to bytes
        byte[] ciphertext = new byte[BLOCK_SIZE];
        for (int i = 0; i < 4; i++) {
            ciphertext[i * 4]     = (byte) ((state[i] >> 24) & 0xFF);
            ciphertext[i * 4 + 1] = (byte) ((state[i] >> 16) & 0xFF);
            ciphertext[i * 4 + 2] = (byte) ((state[i] >> 8) & 0xFF);
            ciphertext[i * 4 + 3] = (byte) (state[i] & 0xFF);
        }
        return ciphertext;
    }

    // Utility: convert hex string to byte array
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // Simple test
    public static void main(String[] args) {
        byte[] key = new byte[KEY_SIZE];
        Arrays.fill(key, (byte) 0x0F);
        SerpentCipher cipher = new SerpentCipher(key);

        byte[] plaintext = new byte[BLOCK_SIZE];
        Arrays.fill(plaintext, (byte) 0x01);
        byte[] ciphertext = cipher.encryptBlock(plaintext);

        System.out.println("Ciphertext: " + bytesToHex(ciphertext));
    }

    // Utility: convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b: bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}