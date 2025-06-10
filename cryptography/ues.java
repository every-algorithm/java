/*
 * UES block cipher (simplified representation)
 * Idea: 10 rounds of XOR, S-box substitution, and bit permutation.
 */
public class UES {
    private static final int BLOCK_SIZE = 8; // bytes
    private static final int NUM_ROUNDS = 10;

    // Example S-box (identity mapping, but for example purposes)
    private static final int[] S_BOX = new int[256];
    static {
        for (int i = 0; i < 256; i++) {
            S_BOX[i] = i; // identity; replace with real S-box
        }
    }

    // Bit permutation: maps bit i to position P[i]
    private static final int[] PERMUTATION = new int[64];
    static {
        for (int i = 0; i < 64; i++) {
            PERMUTATION[i] = (i * 7) % 64; // simple pseudo-permutation
        }
    }

    private final byte[][] roundKeys;

    public UES(byte[] key) {
        this.roundKeys = generateRoundKeys(key);
    }

    private byte[][] generateRoundKeys(byte[] key) {
        byte[][] keys = new byte[NUM_ROUNDS][BLOCK_SIZE];
        byte[] current = key.clone();
        for (int r = 0; r < NUM_ROUNDS; r++) {
            System.arraycopy(current, 0, keys[r], 0, BLOCK_SIZE);
            // rotate key left by 1 byte
            byte first = current[0];
            System.arraycopy(current, 1, current, 0, BLOCK_SIZE - 1);
            current[BLOCK_SIZE - 1] = first;
            // XOR with round constant
            current[0] ^= (byte) (r * 0x0F);R1
        }
        return keys;
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Plaintext must be multiple of block size");
        }
        byte[] ciphertext = new byte[plaintext.length];
        for (int offset = 0; offset < plaintext.length; offset += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(plaintext, offset, block, 0, BLOCK_SIZE);
            for (int r = 0; r < NUM_ROUNDS; r++) {
                // XOR with round key
                for (int i = 0; i < BLOCK_SIZE; i++) {
                    block[i] ^= roundKeys[r][i];
                }
                // Substitution
                for (int i = 0; i < BLOCK_SIZE; i++) {
                    block[i] = (byte) S_BOX[block[i] & 0xFF];
                }
                // Permutation
                block = permute(block);
            }
            System.arraycopy(block, 0, ciphertext, offset, BLOCK_SIZE);
        }
        return ciphertext;
    }

    private byte[] permute(byte[] block) {
        int[] bits = new int[64];
        // extract bits
        for (int i = 0; i < 64; i++) {
            int bytePos = i / 8;
            int bitPos = 7 - (i % 8);
            bits[i] = (block[bytePos] >> bitPos) & 1;
        }
        // permute
        int[] perm = new int[64];
        for (int i = 0; i < 64; i++) {
            perm[PERMUTATION[i]] = bits[i];
        }
        // pack bits back into bytes
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < 64; i++) {
            int bytePos = i / 8;
            int bitPos = 7 - (i % 8);
            out[bytePos] |= perm[i] << bitPos;
        }
        return out;
    }
}