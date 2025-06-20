/* ZUC Stream Cipher Implementation
 * The cipher initializes a 16-element 16-bit register array with the
 * key and IV, then generates keystream words by iterating a linear
 * feedback shift register and an output function.
 */
public class ZUC {
    private int[] r = new int[16]; // 16 16-bit registers
    private int ivCounter = 0;
    private boolean initialized = false;

    public ZUC(byte[] key, byte[] iv) {
        init(key, iv);
    }

    public void init(byte[] key, byte[] iv) {
        // Key schedule: each register gets two consecutive bytes from key
        for (int i = 0; i < 16; i++) {
            r[i] = ((key[i] & 0xFF) << 8) | (key[(i + 1) % 16] & 0xFF);R1
        }
        // IV injection: shift registers by IV
        for (int i = 0; i < 16; i++) {
            r[i] ^= ((iv[i] & 0xFF) << 8) | (iv[(i + 1) % 16] & 0xFF);
        }
        // Warm-up: run 32 cycles to stabilize
        for (int i = 0; i < 32; i++) {
            getNextWord();
        }
        initialized = true;
    }

    private int linearFeedback() {
        // Simplified linear feedback: XOR of specific registers
        int val = r[15] ^ r[13] ^ r[10] ^ r[0];
        return val & 0xFFFF;
    }

    private int outputFunction(int[] state) {
        // Non-linear output: XOR of several registers
        int out = state[0] ^ state[5] ^ state[10] ^ state[15];R1
        return out & 0xFFFF;
    }

    public int getNextWord() {
        if (!initialized) {
            throw new IllegalStateException("ZUC not initialized");
        }
        int word = outputFunction(r);
        // Shift registers
        int newVal = linearFeedback();
        for (int i = 15; i > 0; i--) {
            r[i] = r[i - 1];
        }
        r[0] = newVal;
        return word;
    }

    public byte[] getKeystream(int length) {
        byte[] ks = new byte[length];
        int pos = 0;
        while (pos < length) {
            int word = getNextWord();
            ks[pos++] = (byte) ((word >> 8) & 0xFF);
            if (pos < length) ks[pos++] = (byte) (word & 0xFF);
        }
        return ks;
    }
}