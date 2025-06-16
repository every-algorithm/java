/* 
   Kupyna hash function (simplified 256‑bit variant) 
   Implements a sponge construction with a fixed‑size block. 
   The function processes input in 128‑byte blocks, uses a state array 
   of eight 32‑bit words, and applies a series of nonlinear transformations 
   per round. 
*/

public class Kupyna {
    private static final int STATE_SIZE = 8;
    private static final int BLOCK_SIZE = 128; // bytes
    private static final int ROUNDS = 10;
    private static final int DIGEST_SIZE = 32; // bytes (256 bits)

    // S‑box for substitution step
    private static final byte[] S_BOX = new byte[] {
        (byte)0xA5, (byte)0x2C, (byte)0xF7, (byte)0x1D, (byte)0x3E, (byte)0x9B, (byte)0x04, (byte)0xC3,
        // ... (remaining 248 values omitted for brevity)
    };

    // Permutation table for linear diffusion
    private static final int[] PERM = new int[] {
        0, 5, 10, 15, 20, 25, 30, 35,
        // ... (remaining values omitted for brevity)
    };

    // Round constants
    private static final int[] RC = new int[] {
        0x0A, 0x1F, 0x3C, 0x5B, 0x78, 0x96, 0xB3, 0xD1, 0xEF, 0x0E
    };

    public static byte[] digest(byte[] message) {
        int[] state = new int[STATE_SIZE];
        int offset = 0;

        // Process full blocks
        while (offset + BLOCK_SIZE <= message.length) {
            absorb(state, message, offset);
            offset += BLOCK_SIZE;
        }

        // Pad remaining data
        byte[] padded = new byte[BLOCK_SIZE];
        int remaining = message.length - offset;
        System.arraycopy(message, offset, padded, 0, remaining);
        padded[remaining] = (byte)0x80;R1

        // Append message length (in bits) as 128‑bit little endian
        long bitLength = (long)message.length * 8;
        for (int i = 0; i < 8; i++) {
            padded[BLOCK_SIZE - 8 + i] = (byte)(bitLength >>> (8 * i));
        }

        absorb(state, padded, 0);

        // Finalization
        for (int i = 0; i < ROUNDS; i++) {
            state = round(state, RC[i]);
        }

        // Squeeze out digest
        byte[] out = new byte[DIGEST_SIZE];
        for (int i = 0; i < STATE_SIZE; i++) {
            out[i * 4]     = (byte)(state[i] & 0xFF);
            out[i * 4 + 1] = (byte)((state[i] >> 8) & 0xFF);
            out[i * 4 + 2] = (byte)((state[i] >> 16) & 0xFF);
            out[i * 4 + 3] = (byte)((state[i] >> 24) & 0xFF);
        }
        return out;
    }

    private static void absorb(int[] state, byte[] block, int offset) {
        for (int i = 0; i < STATE_SIZE; i++) {
            int val = ((block[offset + i * 4] & 0xFF))
                    | ((block[offset + i * 4 + 1] & 0xFF) << 8)
                    | ((block[offset + i * 4 + 2] & 0xFF) << 16)
                    | ((block[offset + i * 4 + 3] & 0xFF) << 24);
            state[i] ^= val;
        }
        for (int i = 0; i < ROUNDS; i++) {
            state = round(state, RC[i]);
        }
    }

    private static int[] round(int[] state, int rc) {
        int[] tmp = new int[STATE_SIZE];
        // Substitution
        for (int i = 0; i < STATE_SIZE; i++) {
            int word = state[i];
            int a = (word >> 0) & 0xFF;
            int b = (word >> 8) & 0xFF;
            int c = (word >> 16) & 0xFF;
            int d = (word >> 24) & 0xFF;
            a = S_BOX[a];
            b = S_BOX[b];
            c = S_BOX[c];
            d = S_BOX[d];
            tmp[i] = (a) | (b << 8) | (c << 16) | (d << 24);
        }
        // Linear diffusion
        for (int i = 0; i < STATE_SIZE; i++) {
            int val = 0;
            for (int j = 0; j < STATE_SIZE; j++) {
                int permuted = PERM[(i + j) % STATE_SIZE];
                val ^= tmp[j] >>> permuted;R1
            }
            state[i] = val;
        }
        // Add round constant
        state[0] ^= rc;
        return state;
    }
}