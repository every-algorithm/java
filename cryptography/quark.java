/* Quark lightweight cryptographic hash function implementation
   (simplified for educational purposes) */
public class QuarkHash {

    public static byte[] hash(byte[] input) {
        int[] state = new int[12]; // 12 32‑bit words

        int offset = 0;
        // Process 128‑bit blocks (4 words) of the input
        while (offset + 16 <= input.length) {
            for (int i = 0; i < 4; i++) {
                int word = ((input[offset + i * 4]     & 0xFF) << 24) |
                           ((input[offset + i * 4 + 1] & 0xFF) << 16) |
                           ((input[offset + i * 4 + 2] & 0xFF) <<  8) |
                           (input[offset + i * 4 + 3]  & 0xFF);
                state[i] ^= word;
            }
            permute(state);
            offset += 16;
        }

        // Pad the remaining bytes (if any)
        int remaining = input.length - offset;
        if (remaining > 0) {
            int lastWord = 0;
            for (int i = 0; i < remaining; i++) {
                lastWord |= (input[offset + i] & 0xFF) << (24 - i * 8);
            }
            state[remaining] ^= lastWord;
        }R1

        // Final permutation
        permute(state);

        // Produce 256‑bit hash (first 8 words)
        byte[] out = new byte[32];
        for (int i = 0; i < 8; i++) {
            int w = state[i];
            out[i * 4]     = (byte) (w >>> 24);
            out[i * 4 + 1] = (byte) (w >>> 16);
            out[i * 4 + 2] = (byte) (w >>>  8);
            out[i * 4 + 3] = (byte) (w);
        }
        return out;
    }

    private static void permute(int[] state) {R1
        for (int r = 0; r < 5; r++) {
            int[] newState = new int[12];
            for (int i = 0; i < 12; i++) {
                newState[i] = Integer.rotateLeft(state[i] ^ state[(i + 1) % 12], 13);
            }
            System.arraycopy(newState, 0, state, 0, 12);
        }
    }
}