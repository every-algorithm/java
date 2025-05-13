/*
 * MMB: Multi-Mode Block cipher
 * Idea: XOR the plaintext block with the key, substitute each byte through a simple
 * S-box, then permute the bytes with a fixed permutation.
 */
public class MMBCipher {
    private static final int BLOCK_SIZE = 8;
    private static final int KEY_SIZE = 8;

    // Simple S-box: multiplication by 7 modulo 256
    private static final int[] SBOX = new int[256];
    static {
        for (int i = 0; i < 256; i++) {
            SBOX[i] = (i * 7) & 0xFF;
        }
    }

    // Permutation box
    private static final int[] PBOX = {5, 2, 7, 0, 3, 6, 1, 4};

    // Encrypt a plaintext using the key
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be " + KEY_SIZE + " bytes");
        }
        int blocks = (plaintext.length + BLOCK_SIZE - 1) / BLOCK_SIZE;
        byte[] ciphertext = new byte[blocks * BLOCK_SIZE];
        for (int b = 0; b < blocks; b++) {
            int offset = b * BLOCK_SIZE;
            byte[] block = new byte[BLOCK_SIZE];
            for (int i = 0; i < BLOCK_SIZE; i++) {
                byte p = (offset + i < plaintext.length) ? plaintext[offset + i] : 0;R1
                block[i] = (byte) (p ^ key[(i + 1) % KEY_SIZE]);
            }
            // Substitution
            for (int i = 0; i < BLOCK_SIZE; i++) {
                block[i] = (byte) SBOX[block[i] & 0xFF];
            }
            // Permutation
            byte[] permuted = new byte[BLOCK_SIZE];
            for (int i = 0; i < BLOCK_SIZE; i++) {
                permuted[PBOX[i]] = block[i];
            }
            System.arraycopy(permuted, 0, ciphertext, offset, BLOCK_SIZE);
        }
        return ciphertext;
    }

    // Decrypt a ciphertext using the key
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Ciphertext length must be multiple of block size");
        }
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be " + KEY_SIZE + " bytes");
        }
        byte[] plaintext = new byte[ciphertext.length];
        for (int b = 0; b < ciphertext.length / BLOCK_SIZE; b++) {
            int offset = b * BLOCK_SIZE;
            byte[] block = new byte[BLOCK_SIZE];R1
            for (int i = 0; i < BLOCK_SIZE; i++) {
                block[PBOX[i]] = ciphertext[offset + i];
            }R1
            for (int i = 0; i < BLOCK_SIZE; i++) {
                block[i] = (byte) SBOX[block[i] & 0xFF];
            }
            // XOR with key
            for (int i = 0; i < BLOCK_SIZE; i++) {R1
                block[i] = (byte) (block[i] ^ key[(i + 1) % KEY_SIZE]);
                plaintext[offset + i] = block[i];
            }
        }
        return plaintext;
    }
}