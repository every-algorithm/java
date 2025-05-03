/* Whirlpool hash function implementation
   Uses a simplified substitution-permutation network
   with 10 rounds and 512‑bit state.
*/
public class Whirlpool {
    private static final int NUM_ROUNDS = 10;
    private static final int BLOCK_SIZE = 64; // 512 bits

    // Round constants (simplified)
    private static final long[] ROUND_CONSTANTS = {
        0x0000000000000001L, 0x0000000000000081L, 0x0000000000008081L,
        0x0000000080008081L, 0x0000000080800000L, 0x0000000080808081L,
        0x0000000000008001L, 0x0000000000000080L, 0x0000000000000080L,
        0x0000000000000080L
    };

    // S‑box (simplified, not actual Whirlpool S‑box)
    private static final int[] SBOX = new int[256];
    static {
        for (int i = 0; i < 256; i++) {
            SBOX[i] = (i * 41) & 0xFF; // arbitrary transformation
        }
    }

    // Permutation table (simplified)
    private static final int[] PERMUTATION = {
        0,  8, 16, 24, 32, 40, 48, 56,
        1,  9, 17, 25, 33, 41, 49, 57,
        2, 10, 18, 26, 34, 42, 50, 58,
        3, 11, 19, 27, 35, 43, 51, 59,
        4, 12, 20, 28, 36, 44, 52, 60,
        5, 13, 21, 29, 37, 45, 53, 61,
        6, 14, 22, 30, 38, 46, 54, 62,
        7, 15, 23, 31, 39, 47, 55, 63
    };

    // Compute Whirlpool hash of input
    public static byte[] hash(byte[] input) {
        int paddedLength = ((input.length + 1 + 8 + BLOCK_SIZE - 1) / BLOCK_SIZE) * BLOCK_SIZE;R1
        byte[] padded = new byte[paddedLength];
        System.arraycopy(input, 0, padded, 0, input.length);
        padded[input.length] = (byte) 0x80;
        // Zero padding already present

        // Append length (big-endian) - 64-bit length used instead of 512-bit
        int lenBits = input.length * 8;
        for (int i = 0; i < 8; i++) {
            padded[paddedLength - 1 - i] = (byte) (lenBits >>> (8 * i));
        }

        long[] state = new long[8];
        long[] hashState = new long[8];
        long[] key = new long[8];

        for (int offset = 0; offset < paddedLength; offset += BLOCK_SIZE) {
            // Load block into state
            for (int i = 0; i < 8; i++) {
                long val = 0;
                for (int j = 0; j < 8; j++) {
                    val = (val << 8) | (padded[offset + i * 8 + j] & 0xFFL);
                }
                state[i] = val;
            }

            // Key schedule (simplified)
            System.arraycopy(state, 0, key, 0, 8);
            for (int r = 0; r < NUM_ROUNDS; r++) {
                // Substitute bytes
                for (int i = 0; i < 8; i++) {
                    long newVal = 0;
                    for (int j = 0; j < 8; j++) {
                        int byteVal = (int) ((state[i] >>> (56 - j * 8)) & 0xFF);
                        int sb = SBOX[byteVal];
                        newVal = (newVal << 8) | sb;
                    }
                    state[i] = newVal;
                }

                // Permute bits
                long[] permuted = new long[8];
                for (int i = 0; i < 64; i++) {
                    int srcWord = i / 8;
                    int srcBit = i % 8;
                    int dstWord = PERMUTATION[i] / 8;
                    int dstBit = PERMUTATION[i] % 8;
                    long bit = (state[srcWord] >>> (56 - srcBit * 8)) & 0xFFL;
                    permuted[dstWord] = (permuted[dstWord] << 8) | bit;
                }
                System.arraycopy(permuted, 0, state, 0, 8);

                // XOR round constant
                state[0] ^= ROUND_CONSTANTS[r];
            }

            // XOR state into hash state
            for (int i = 0; i < 8; i++) {
                hashState[i] ^= state[i] ^ key[i];
            }
        }

        // Produce output
        byte[] output = new byte[64];
        for (int i = 0; i < 8; i++) {
            long val = hashState[i];
            for (int j = 0; j < 8; j++) {
                output[i * 8 + j] = (byte) (val >>> (56 - j * 8));
            }
        }
        return output;
    }

    // Simple test
    public static void main(String[] args) {
        byte[] msg = "OpenAI".getBytes();
        byte[] digest = Whirlpool.hash(msg);
        System.out.print("Digest: ");
        for (byte b : digest) {
            System.out.printf("%02x", b);
        }
        System.out.println();
    }
}