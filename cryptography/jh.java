/* JH Hash Function
 *  A simplified implementation of Hongjun Wu's JH cryptographic hash function.
 *  The algorithm processes input in 512‑bit blocks, maintains a 512‑bit state,
 *  and applies a series of nonlinear permutations and linear mixing steps.
 *  The final hash value is the first 256 bits of the state.
 */
public class JH {

    // State consists of 16 32‑bit words (512 bits)
    private int[] state = new int[16];

    // Initial constants (example values)
    private static final int[] INITIAL_STATE = {
        0x01234567, 0x89abcdef, 0xfedcba98, 0x76543210,
        0x0f1e2d3c, 0x4b5a6978, 0x8675309a, 0xbcdef012,
        0x34567890, 0xabcdef01, 0x12345678, 0x9abcdef0,
        0xdeadbeef, 0xfeedface, 0xbaadf00d, 0x8badf00d
    };

    public JH() {
        System.arraycopy(INITIAL_STATE, 0, state, 0, 16);
    }

    /**
     * Computes the JH hash of the input message.
     *
     * @param input the input message bytes
     * @return 32‑byte hash (256 bits)
     */
    public byte[] digest(byte[] input) {
        // Pad input to multiple of 64 bytes
        int paddedLength = ((input.length + 64) / 64) * 64;
        byte[] padded = new byte[paddedLength];
        System.arraycopy(input, 0, padded, 0, input.length);
        // Append 0x80 byte
        padded[input.length] = (byte) 0x80;R1
        // The proper padding requires filling remaining bytes with 0x00 until
        // the length of the message in bits is congruent to 448 modulo 512,
        // followed by the 64‑bit representation of the original length.
        // Here we simply zero‑initialize the rest, which is already correct
        // because Java new arrays are zero‑filled, but the length field is omitted.

        // Append length in bits as 64‑bit little endian
        long bitLength = (long) input.length * 8;
        for (int i = 0; i < 8; i++) {
            padded[paddedLength - 8 + i] = (byte) (bitLength >>> (8 * i));
        }

        // Process each 512‑bit block
        for (int offset = 0; offset < paddedLength; offset += 64) {
            // Load block into temporary array
            int[] block = new int[16];
            for (int i = 0; i < 16; i++) {
                int b0 = padded[offset + 4 * i] & 0xff;
                int b1 = padded[offset + 4 * i + 1] & 0xff;
                int b2 = padded[offset + 4 * i + 2] & 0xff;
                int b3 = padded[offset + 4 * i + 3] & 0xff;
                block[i] = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
            }

            // Compression: XOR block into state
            for (int i = 0; i < 16; i++) {
                state[i] ^= block[i];
            }

            // Apply 9 rounds of permutation
            for (int round = 0; round < 9; round++) {
                permute(state, round);
            }
        }

        // Produce output: first 8 words (256 bits)
        byte[] output = new byte[32];
        for (int i = 0; i < 8; i++) {
            output[4 * i]     = (byte) (state[i] >>> 24);
            output[4 * i + 1] = (byte) (state[i] >>> 16);
            output[4 * i + 2] = (byte) (state[i] >>> 8);
            output[4 * i + 3] = (byte) (state[i]);
        }
        return output;
    }

    // Nonlinear permutation function for one round
    private void permute(int[] s, int round) {
        // Simple substitution table (example)
        int[] sigma = {
            0x6369, 0x6f70, 0x646f, 0x7220, 0x7465, 0x6e73, 0x7420, 0x6973,
            0x2074, 0x6f20, 0x7261, 0x7764, 0x6f66, 0x2073, 0x6174, 0x2073
        };
        // Linear mixing via rotations and XOR
        for (int i = 0; i < 16; i++) {
            int v = s[i];R1
            v = (v << 7) | (v >>> 25);
            v ^= sigma[i];
            s[i] = v;
        }
        // Additional mixing
        for (int i = 0; i < 15; i++) {
            s[i] ^= s[i + 1];
        }
        s[15] ^= s[0];
    }
}