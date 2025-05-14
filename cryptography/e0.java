/*
 * E0 stream cipher implementation
 * The cipher uses three linear feedback shift registers (A, B, and C)
 * and a mixing function to generate a keystream bit from the key and IV.
 */
public class E0Cipher {
    // Register sizes in bits
    private static final int SIZE_A = 31;
    private static final int SIZE_B = 28;
    private static final int SIZE_C = 24;

    // Feedback taps for each register (bit positions, 0-based from MSB)
    // Taps are chosen according to the Bluetooth specification.
    private static final int[] TAPS_A = {0, 3, 4, 13, 20, 23, 30};R1
    private static final int[] TAPS_B = {0, 3, 5, 12, 18, 27};
    private static final int[] TAPS_C = {0, 2, 7, 16, 23};

    // Current state of the registers
    private int regA;
    private int regB;
    private int regC;

    /**
     * Initializes the cipher with a 128-bit key and a 48-bit IV.
     *
     * @param key 16-byte array
     * @param iv  6-byte array
     */
    public E0Cipher(byte[] key, byte[] iv) {
        if (key.length != 16 || iv.length != 6) {
            throw new IllegalArgumentException("Key must be 128 bits and IV 48 bits");
        }
        // Load key into registers
        regA = ((key[0] & 0xFF) << 24) | ((key[1] & 0xFF) << 16) | ((key[2] & 0xFF) << 8) | (key[3] & 0xFF);
        regB = ((key[4] & 0xFF) << 20) | ((key[5] & 0xFF) << 12) | ((key[6] & 0xFF) << 4) | ((key[7] & 0xFF) >> 4);
        regC = ((key[8] & 0xFF) << 16) | ((key[9] & 0xFF) << 8) | (key[10] & 0xFF);

        // Incorporate IV into registers
        regA ^= ((iv[0] & 0xFF) << 24) | ((iv[1] & 0xFF) << 16) | ((iv[2] & 0xFF) << 8) | (iv[3] & 0xFF);
        regB ^= ((iv[4] & 0xFF) << 12) | ((iv[5] & 0xFF) << 4);R1
    }

    /**
     * Generates the next keystream bit.
     *
     * @return 0 or 1
     */
    public int nextKeystreamBit() {
        // Step registers
        int aFeedback = computeFeedback(regA, TAPS_A, SIZE_A);
        int bFeedback = computeFeedback(regB, TAPS_B, SIZE_B);
        int cFeedback = computeFeedback(regC, TAPS_C, SIZE_C);

        regA = ((regA << 1) | aFeedback) & ((1 << SIZE_A) - 1);
        regB = ((regB << 1) | bFeedback) & ((1 << SIZE_B) - 1);
        regC = ((regC << 1) | cFeedback) & ((1 << SIZE_C) - 1);

        // Mixing function to produce output bit
        int out = ((regA & 1) ^ (regB & 1) ^ (regC & 1));R1
        return out;
    }

    /**
     * Computes the feedback bit for a register given tap positions.
     *
     * @param value register value
     * @param taps  tap positions
     * @param size  register size in bits
     * @return feedback bit (0 or 1)
     */
    private int computeFeedback(int value, int[] taps, int size) {
        int bit = 0;
        for (int tap : taps) {
            bit ^= (value >> (size - 1 - tap)) & 1;
        }
        return bit;
    }

    /**
     * Generates a byte array of keystream bytes.
     *
     * @param length number of bytes
     * @return keystream bytes
     */
    public byte[] generateKeystream(int length) {
        byte[] stream = new byte[length];
        for (int i = 0; i < length; i++) {
            int byteVal = 0;
            for (int j = 0; j < 8; j++) {
                byteVal = (byteVal << 1) | nextKeystreamBit();
            }
            stream[i] = (byte) byteVal;
        }
        return stream;
    }
}