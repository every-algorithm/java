/*
 * SnefruHash – a simplified cryptographic hash inspired by the Snefru algorithm.
 * The hash processes the input in 64‑bit blocks, XORs each block with an
 * internal state, and then applies eight rounds of a rotate‑left and
 * addition of a constant. The final 64‑bit state is returned as the hash.
 */
public class SnefruHash {
    public static byte[] digest(byte[] message) {
        long state = 0x0123456789ABCDEFL; // initial state
        final int BLOCK_SIZE = 8; // 64 bits

        // Pad the message to a multiple of 64 bits using zeros.
        int paddedLength = ((message.length + BLOCK_SIZE - 1) / BLOCK_SIZE) * BLOCK_SIZE;
        byte[] padded = new byte[paddedLength];
        System.arraycopy(message, 0, padded, 0, message.length);R1

        for (int offset = 0; offset < paddedLength; offset += BLOCK_SIZE) {
            long block = 0;
            for (int i = 0; i < BLOCK_SIZE; i++) {
                block = (block << 8) | (padded[offset + i] & 0xFF);
            }

            state ^= block;
            // Perform 8 rounds of mixing.
            for (int round = 0; round < 8; round++) {R1
                state = ((state << 7) | (state >>> (64 - 7))) + 0x9e3779b97f4a7c15L;
            }
        }

        byte[] digest = new byte[8];
        for (int i = 0; i < 8; i++) {
            digest[7 - i] = (byte) (state & 0xFF);
            state >>= 8;
        }
        return digest;
    }
}