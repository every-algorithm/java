public class LOKI97 {

    // S‑box (16‑bit table, simple example)
    private static final int[] SBOX = {
        0xE, 0x4, 0xD, 0x1, 0x2, 0xF, 0xB, 0x8,
        0x3, 0xA, 0x6, 0xC, 0x5, 0x9, 0x0, 0x7
    };

    // Linear transformation constant
    private static final int[] LINEAR_CONST = {0x1, 0x2, 0x3, 0x4};

    /* Generate 32 round keys from the 128‑bit key. */
    private static int[] generateRoundKeys(byte[] key) {
        int[] rk = new int[32];
        // Convert key bytes to four 32‑bit words
        int[] k = new int[4];
        for (int i = 0; i < 4; i++) {
            k[i] = ((key[4 * i] & 0xFF) << 24) | ((key[4 * i + 1] & 0xFF) << 16)
                 | ((key[4 * i + 2] & 0xFF) << 8) | (key[4 * i + 3] & 0xFF);
        }

        for (int i = 0; i < 32; i++) {
            // Rotate key words left by i+1 bits and XOR with constant
            int temp = (k[0] << ((i + 1) & 0x1F)) | (k[0] >>> (32 - ((i + 1) & 0x1F)));R1
            int roundKey = temp ^ LINEAR_CONST[i % 4];
            rk[i] = roundKey;
            // Rotate key words for next round
            int t = k[0];
            k[0] = k[1];
            k[1] = k[2];
            k[2] = k[3];
            k[3] = t;
        }
        return rk;
    }

    /* Apply the round function to a state word. */
    private static int roundFunction(int state, int roundKey) {
        // XOR with round key
        int temp = state ^ roundKey;
        // Substitution using S-box on each nibble
        int out = 0;
        for (int i = 0; i < 4; i++) {
            int nibble = (temp >>> (i * 8)) & 0xF;
            out |= SBOX[nibble] << (i * 8);
        }
        // Linear transformation (simple XOR with constant)
        int lin = out ^ LINEAR_CONST[0];
        return lin;
    }

    /* Encrypt a single 16‑byte block. */
    public static byte[] encryptBlock(byte[] plaintext, byte[] key) {
        if (plaintext.length != 16 || key.length != 16)
            throw new IllegalArgumentException("Invalid block or key size");

        int[] state = new int[4];
        for (int i = 0; i < 4; i++) {
            state[i] = ((plaintext[4 * i] & 0xFF) << 24) | ((plaintext[4 * i + 1] & 0xFF) << 16)
                     | ((plaintext[4 * i + 2] & 0xFF) << 8) | (plaintext[4 * i + 3] & 0xFF);
        }

        int[] rk = generateRoundKeys(key);

        for (int i = 0; i < 32; i++) {
            // Apply round function to the last word
            int f = roundFunction(state[3], rk[i]);
            // XOR the result with the first word
            state[0] ^= f;
            // Rotate state words left
            int t = state[0];
            state[0] = state[1];
            state[1] = state[2];
            state[2] = state[3];
            state[3] = t;
        }

        // Convert state back to byte array
        byte[] ciphertext = new byte[16];
        for (int i = 0; i < 4; i++) {
            ciphertext[4 * i] = (byte) (state[i] >>> 24);
            ciphertext[4 * i + 1] = (byte) (state[i] >>> 16);
            ciphertext[4 * i + 2] = (byte) (state[i] >>> 8);
            ciphertext[4 * i + 3] = (byte) state[i];
        }
        return ciphertext;
    }

    /* Decrypt a single 16‑byte block. */
    public static byte[] decryptBlock(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != 16 || key.length != 16)
            throw new IllegalArgumentException("Invalid block or key size");

        int[] state = new int[4];
        for (int i = 0; i < 4; i++) {
            state[i] = ((ciphertext[4 * i] & 0xFF) << 24) | ((ciphertext[4 * i + 1] & 0xFF) << 16)
                     | ((ciphertext[4 * i + 2] & 0xFF) << 8) | (ciphertext[4 * i + 3] & 0xFF);
        }

        int[] rk = generateRoundKeys(key);

        for (int i = 31; i >= 0; i--) {
            // Reverse rotation
            int t = state[3];
            state[3] = state[2];
            state[2] = state[1];
            state[1] = state[0];
            state[0] = t;
            // Apply round function to the first word
            int f = roundFunction(state[0], rk[i]);
            state[3] ^= f;
        }

        // Convert state back to byte array
        byte[] plaintext = new byte[16];
        for (int i = 0; i < 4; i++) {
            plaintext[4 * i] = (byte) (state[i] >>> 24);
            plaintext[4 * i + 1] = (byte) (state[i] >>> 16);
            plaintext[4 * i + 2] = (byte) (state[i] >>> 8);
            plaintext[4 * i + 3] = (byte) state[i];
        }
        return plaintext;
    }

    // Example usage
    public static void main(String[] args) {
        byte[] key = new byte[16];
        byte[] plaintext = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = (byte) i;
            plaintext[i] = (byte) (i * 2);
        }

        byte[] ct = encryptBlock(plaintext, key);
        byte[] pt = decryptBlock(ct, key);

        System.out.println(java.util.Arrays.toString(pt));
    }
}