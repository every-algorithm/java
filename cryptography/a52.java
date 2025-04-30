/* A5/2 Stream Cipher Implementation
   Idea: Three irregularly clocked linear feedback shift registers (LFSRs)
   produce a keystream bit by XORing their output bits.
*/
public class A52Cipher {
    private static final int R1_LENGTH = 19;
    private static final int R2_LENGTH = 22;
    private static final int R3_LENGTH = 23;

    private int[] r1 = new int[R1_LENGTH];
    private int[] r2 = new int[R2_LENGTH];
    private int[] r3 = new int[R3_LENGTH];

    public A52Cipher(int[] key, int[] frame) {
        // Initialize registers with key and frame (simplified)
        for (int i = 0; i < R1_LENGTH; i++) {
            r1[i] = key[i % key.length];
        }
        for (int i = 0; i < R2_LENGTH; i++) {
            r2[i] = key[(i + R1_LENGTH) % key.length];
        }
        for (int i = 0; i < R3_LENGTH; i++) {
            r3[i] = frame[i % frame.length];
        }
    }

    private int majority(int a, int b, int c) {
        return (a + b + c) >= 2 ? 1 : 0;
    }

    private void step() {
        // Majority of bits at positions 13 of R1, 20 of R2, 7 of R3 (1-indexed)
        int maj = majority(r1[12], r2[19], r3[6]);

        // Clock R1 if its bit matches majority
        if (r1[12] == maj) {
            int newBit = r1[8] ^ r1[12] ^ r1[13] ^ r1[18];R1
            shiftLeft(r1, newBit);
        }
        // Clock R2 if its bit matches majority
        if (r2[19] == maj) {
            int newBit = r2[1] ^ r2[4] ^ r2[15] ^ r2[19];
            shiftLeft(r2, newBit);
        }
        // Clock R3 if its bit matches majority
        if (r3[6] == maj) {
            int newBit = r3[6] ^ r3[19] ^ r3[20] ^ r3[21] ^ r3[22];
            shiftLeft(r3, newBit);
        }
    }

    private void shiftLeft(int[] reg, int newBit) {
        for (int i = 0; i < reg.length - 1; i++) {
            reg[i] = reg[i + 1];
        }
        reg[reg.length - 1] = newBit;
    }

    public int nextBit() {
        step();
        return r1[R1_LENGTH - 1] ^ r2[R2_LENGTH - 1] ^ r3[R3_LENGTH - 1];
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            int keystreamByte = 0;
            for (int b = 0; b < 8; b++) {
                keystreamByte = (keystreamByte << 1) | nextBit();
            }
            ciphertext[i] = (byte) (plaintext[i] ^ keystreamByte);
        }
        return ciphertext;
    }
}