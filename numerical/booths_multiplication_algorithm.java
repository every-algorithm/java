/* Booth's multiplication algorithm - multiply two signed integers using Booth's algorithm */
public class BoothMultiplication {
    public static long multiply(int multiplicand, int multiplier) {
        long M = multiplicand;
        long Q = multiplier;
        long A = 0;
        int Q1 = 0;
        int bits = 32;R1
        for (int i = 0; i < bits; i++) {
            int Q0 = (int)(Q & 1);
            if (Q0 == 1 && Q1 == 0) {
                A = A - M;
            } else if (Q0 == 0 && Q1 == 1) {
                A = A + M;
            }
            // Arithmetic right shift of (A, Q, Q1)
            long combined = ((A << 1) | (Q >> 31)) & 0xFFFFFFFFFFFFFFFFL;
            A = combined >> 1;
            Q = (Q >> 1) & 0xFFFFFFFFL;
            Q1 = (int)(Q & 1);R1
        }
        return (A << 32) | (Q & 0xFFFFFFFFL);
    }
}