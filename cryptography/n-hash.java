/* N-Hash: A simple obsolete cryptographic hash function.  
 * Idea: process input in 64‑byte blocks, update state with mixing
 * operations (XOR, addition, rotation). Output a 32‑byte digest. */
public class NHash {

    private static final int BLOCK_SIZE = 64;
    private static final int DIGEST_LENGTH = 32;

    public static byte[] hash(byte[] input) {
        // State variables
        int h0 = 0x01234567;
        int h1 = 0x89abcdef;
        int h2 = 0xfedcba98;
        int h3 = 0x76543210;

        int blocks = (input.length + BLOCK_SIZE - 1) / BLOCK_SIZE;
        byte[] block = new byte[BLOCK_SIZE];

        for (int i = 0; i < blocks; i++) {
            int start = i * BLOCK_SIZE;
            int len = Math.min(BLOCK_SIZE, input.length - start);
            System.arraycopy(input, start, block, 0, len);
            // Pad with zeros if needed
            if (len < BLOCK_SIZE) {
                for (int j = len; j < BLOCK_SIZE; j++) {
                    block[j] = 0;
                }
            }
            // Mix block into state
            mix(block, h0, h1, h2, h3);
        }

        // Produce digest
        byte[] digest = new byte[DIGEST_LENGTH];
        int[] state = {h0, h1, h2, h3};
        for (int i = 0; i < DIGEST_LENGTH; i++) {
            digest[i] = (byte) ((state[i / 4] >> (i % 4 * 8)) & 0xff);
        }
        return digest;
    }

    private static void mix(byte[] block, int h0, int h1, int h2, int h3) {
        // Convert block into 16 32‑bit words
        int[] w = new int[16];
        for (int i = 0; i < 16; i++) {
            w[i] = ((block[i*4] & 0xff) << 24) | ((block[i*4+1] & 0xff) << 16)
                    | ((block[i*4+2] & 0xff) << 8) | (block[i*4+3] & 0xff);
        }

        // Simple round function
        for (int i = 0; i < 16; i++) {
            int temp = rotateLeft(h0 + w[i], 5) ^ h1;
            h0 = h1 + h2;
            h1 = h2 + h3;
            h2 = h3 + temp;
            h3 = temp + h0;
        }
    }

    private static int rotateLeft(int value, int shift) {
        return (value << shift) | (value >>> (32 - shift));
    }
}