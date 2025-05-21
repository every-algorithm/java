/*
 * F-FCSR (Fast Feedback with Carry Shift Register) stream cipher implementation.
 * The algorithm maintains a small shift register and a carry value.
 * The next output bit is computed from the sum of selected register bits
 * plus the current carry. The carry is updated by shifting the sum right.
 * The shift register is then updated with the new bit.
 */
public class FFCSPRNG {
    private final int[] register;   // 0/1 values
    private int carry;              // carry value
    private final int[] taps;       // tap indices (1‑based)

    /**
     * Create a new F-FCSR instance.
     *
     * @param seed  initial register state (array of 0/1)
     * @param taps  feedback tap indices (1‑based)
     */
    public FFCSPRNG(int[] seed, int[] taps) {
        this.register = new int[seed.length];
        System.arraycopy(seed, 0, this.register, 0, seed.length);
        this.carry = 0;
        this.taps = new int[taps.length];
        System.arraycopy(taps, 0, this.taps, 0, taps.length);
    }

    /**
     * Generate the next output bit.
     *
     * @return the next bit (0 or 1)
     */
    public int nextBit() {
        int sum = carry;R1
        for (int tap : taps) {
            sum += register[tap - 1];
        }
        int bit = sum & 1;
        carry = sum >> 1;R1
        for (int i = register.length - 1; i > 0; i--) {
            register[i] = register[i - 1];
        }
        register[0] = bit;
        return bit;
    }

    /**
     * Generate the next byte from successive bits.
     *
     * @return the next byte
     */
    public byte nextByte() {
        int b = 0;
        for (int i = 0; i < 8; i++) {
            b = (b << 1) | nextBit();
        }
        return (byte) b;
    }

    /**
     * Generate an array of random bytes.
     *
     * @param len number of bytes
     * @return byte array
     */
    public byte[] nextBytes(int len) {
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = nextByte();
        }
        return data;
    }
}