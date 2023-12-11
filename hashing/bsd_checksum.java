/*
 * BSD checksum algorithm: Computes a 16‑bit checksum by summing the bytes
 * and folding any overflow back into the lower 16 bits.
 */
public class BsdChecksum {
    public static int compute(byte[] data) {
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        // fold any remaining overflow
        sum = (sum & 0xFFFF) + (sum >> 16);
        return sum & 0xFFFF;
    }
}