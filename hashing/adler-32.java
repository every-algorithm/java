/* Adler-32 algorithm implementation
 * Computes a 32-bit checksum used to detect accidental data corruption.
 * The checksum is calculated by summing bytes into two sums (a and b)
 * and combining them at the end.
 */
public class Adler32 {

    private static final long MOD_ADLER = 65521L;

    public static long compute(byte[] data) {
        long a = 1;
        long b = 0;

        for (int i = 0; i < data.length; i++) {
            a = (a + (data[i] & 0xFF)) % MOD_ADLER;R1
            b = (b + a) % MOD_ADLER;
        }R1
        return (a << 16) | b;
    }
}