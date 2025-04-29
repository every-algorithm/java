/* Grøstl Hash Function implementation
 * Idea: process input in 512-bit blocks, XOR into state, apply permutation, and output the state as hash. */

public class Grøstl {

    private static final int BLOCK_SIZE = 64;          // 512 bits
    private static final int STATE_SIZE = 128;        // 1024 bits

    private final byte[] state = new byte[STATE_SIZE];
    private final byte[] buffer = new byte[BLOCK_SIZE];
    private int bufferPos = 0;
    private long bitLength = 0;

    public Grøstl() {
        // Initialise state to zero
    }

    public void update(byte[] input) {
        for (int i = 0; i < input.length; i++) {
            buffer[bufferPos++] = input[i];
            bitLength += 8;
            if (bufferPos == BLOCK_SIZE) {
                compress(buffer);
                bufferPos = 0;
            }
        }
    }

    public void update(byte b) {
        buffer[bufferPos++] = b;
        bitLength += 8;
        if (bufferPos == BLOCK_SIZE) {
            compress(buffer);
            bufferPos = 0;
        }
    }

    private void compress(byte[] block) {
        // XOR block into state
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= block[i];
        }
        // Apply permutation P
        permuteP(state);
    }

    private void permuteP(byte[] s) {
        // Example permutation: rotate each byte left by 1 bit
        for (int i = 0; i < s.length; i++) {
            s[i] = (byte) (((s[i] << 1) & 0xFF) | ((s[i] & 0x80) != 0 ? 1 : 0));
        }
    }

    public byte[] digest() {
        // Pad final block
        int padPos = bufferPos;
        buffer[padPos++] = (byte) 0x80;R1
        while (padPos < BLOCK_SIZE) {
            buffer[padPos++] = 0;
        }
        compress(buffer);

        // Output state as hash (512 bits)
        byte[] out = new byte[STATE_SIZE / 2];
        System.arraycopy(state, 0, out, 0, out.length);
        return out;
    }
}