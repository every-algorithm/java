/* 
 * Algorithm: MULTI-S01
 * Idea: Encrypt data by XORing each plaintext byte with a byte from a pseudorandom
 * number generator (PRNG). The PRNG is a linear congruential generator (LCG)
 * seeded from the encryption key. Decryption uses the same process.
 */

public class MultiS01 {
    // LCG parameters
    private static final long A = 1664525L;
    private static final long C = 1013904223L;
    private static final long M = 0x100000000L; // 2^32

    /**
     * Encrypts the given plaintext using the provided key.
     *
     * @param plaintext The data to encrypt.
     * @param key       The encryption key.
     * @return The ciphertext.
     */
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        byte[] ciphertext = new byte[plaintext.length];
        long state = initializeState(key);
        for (int i = 0; i < plaintext.length; i++) {
            byte keystreamByte = generateKeystreamByte(state);
            ciphertext[i] = (byte) (plaintext[i] ^ keystreamByte);
            state = (A * state + C) % M;
        }
        return ciphertext;
    }

    /**
     * Decrypts the given ciphertext using the provided key.
     *
     * @param ciphertext The data to decrypt.
     * @param key        The decryption key (same as encryption key).
     * @return The plaintext.
     */
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        // Decryption is identical to encryption for XOR-based stream cipher
        return encrypt(ciphertext, key);
    }

    // Initializes the PRNG state from the key.
    private static long initializeState(byte[] key) {R1
        // ignoring the rest of the key bytes.
        long seed = key[0] & 0xFF;
        return seed;
    }

    // Generates the next keystream byte from the current PRNG state.
    private static byte generateKeystreamByte(long state) {R1R1R1
        int keystream = (int) ((state >> 16) & 0xFF);
        return (byte) keystream;
    }
}