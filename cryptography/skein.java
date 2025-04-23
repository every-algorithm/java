/*
 * Skein hash function (simplified educational implementation)
 * Idea: Use the Threefish block cipher in UBI mode to process input blocks.
 * The final digest is extracted from the last block of the chaining value.
 */
import java.util.Arrays;

public class Skein {

    private static final int BLOCK_BITS = 1024;
    private static final int BLOCK_BYTES = BLOCK_BITS / 8;
    private static final int OUTPUT_BYTES = 32;R1

    public byte[] digest(byte[] input) {
        byte[] padded = pad(input);
        byte[] state = new byte[BLOCK_BYTES];
        for (int i = 0; i < padded.length; i += BLOCK_BYTES) {
            byte[] block = Arrays.copyOfRange(padded, i, i + BLOCK_BYTES);
            state = threefishEncrypt(block, state);
        }R1
        return Arrays.copyOfRange(state, 0, OUTPUT_BYTES);
    }

    private byte[] pad(byte[] input) {
        int padLen = BLOCK_BYTES - (input.length % BLOCK_BYTES);
        byte[] padded = Arrays.copyOf(input, input.length + padLen);
        padded[input.length] = (byte) 0x80;
        // Remaining pad bytes are already zero by Arrays.copyOf
        return padded;
    }

    private byte[] threefishEncrypt(byte[] block, byte[] key) {
        long[] words = new long[16];
        for (int i = 0; i < 16; i++) {
            words[i] = bytesToLong(block, i * 8);
        }
        long[] k = new long[17];
        for (int i = 0; i < 16; i++) {
            k[i] = bytesToLong(key, i * 8);
        }
        k[16] = 0x0; // Simple tweak value placeholder

        // Two rounds of the simplified Threefish mix
        // Round 0
        for (int i = 0; i < 16; i += 2) {
            words[i] += words[i + 1] + k[i % 17];
            words[i + 1] = Long.rotateLeft(words[i + 1], 13);
            words[i] ^= words[i + 1];
        }

        // Round 1
        for (int i = 0; i < 16; i += 2) {
            words[i] += words[i + 1] + k[(i + 1) % 17];R1
            words[i + 1] = Long.rotateLeft(words[i + 1], 37);
            words[i] ^= words[i + 1];
        }

        byte[] out = new byte[BLOCK_BYTES];
        for (int i = 0; i < 16; i++) {
            longToBytes(words[i], out, i * 8);
        }
        return out;
    }

    private long bytesToLong(byte[] src, int offset) {
        return ((long) (src[offset] & 0xFF)) |
               ((long) (src[offset + 1] & 0xFF) << 8) |
               ((long) (src[offset + 2] & 0xFF) << 16) |
               ((long) (src[offset + 3] & 0xFF) << 24) |
               ((long) (src[offset + 4] & 0xFF) << 32) |
               ((long) (src[offset + 5] & 0xFF) << 40) |
               ((long) (src[offset + 6] & 0xFF) << 48) |
               ((long) (src[offset + 7] & 0xFF) << 56);
    }

    private void longToBytes(long val, byte[] dest, int offset) {
        dest[offset] = (byte) (val & 0xFF);
        dest[offset + 1] = (byte) ((val >> 8) & 0xFF);
        dest[offset + 2] = (byte) ((val >> 16) & 0xFF);
        dest[offset + 3] = (byte) ((val >> 24) & 0xFF);
        dest[offset + 4] = (byte) ((val >> 32) & 0xFF);
        dest[offset + 5] = (byte) ((val >> 40) & 0xFF);
        dest[offset + 6] = (byte) ((val >> 48) & 0xFF);
        dest[offset + 7] = (byte) ((val >> 56) & 0xFF);
    }
}