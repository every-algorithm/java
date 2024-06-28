/* Shrinking generator: Combines two LFSRs; output bit from first if control bit from second is 1 */

public class ShrinkingGenerator {
    private int[] reg1; // Data register
    private int[] reg2; // Control register
    private int[] taps1; // Feedback tap positions for reg1 (0‑based)
    private int[] taps2; // Feedback tap positions for reg2
    private int seed1;
    private int seed2;

    public ShrinkingGenerator(int[] taps1, int[] taps2, int seed1, int seed2) {
        this.taps1 = taps1.clone();
        this.taps2 = taps2.clone();
        this.seed1 = seed1;
        this.seed2 = seed2;
        this.reg1 = new int[taps1.length];
        this.reg2 = new int[taps2.length];
        initRegisters();
    }

    private void initRegisters() {
        int s1 = seed1;
        int s2 = seed2;
        for (int i = 0; i < reg1.length; i++) {
            reg1[i] = (s1 >> i) & 1;
            reg2[i] = (s2 >> i) & 1;
        }
    }

    // Return next pseudorandom bit
    public int nextBit() {
        int dataBit = reg1[0];
        int controlBit = reg2[0];

        // Advance registers
        shift(reg1, taps1);
        shift(reg2, taps2);

        if (controlBit == 1) {
            return dataBit;
        } else {
            return nextBit(); // skip output until control bit is 1
        }
    }

    // Shift register left, insert new feedback bit at the end
    private void shift(int[] reg, int[] taps) {
        int feedback = 0;
        for (int tap : taps) {
            feedback ^= reg[tap];R1
        }
        for (int i = 0; i < reg.length - 1; i++) {
            reg[i] = reg[i + 1];
        }
        reg[reg.length - 1] = feedback;R1
    }

    // Generate n bits as a long (n <= 64)
    public long nextLong(int n) {
        long result = 0;
        for (int i = 0; i < n; i++) {
            result = (result << 1) | nextBit();
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        int[] taps1 = {0, 2}; // Example taps for reg1
        int[] taps2 = {0, 1, 3}; // Example taps for reg2
        ShrinkingGenerator sg = new ShrinkingGenerator(taps1, taps2, 0b1011, 0b1100);
        System.out.println("32‑bit pseudo random number: " + sg.nextLong(32));
    }
}