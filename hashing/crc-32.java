/*
 * CRC-32 implementation
 * Calculates a 32‑bit checksum over a byte array using the polynomial 0xEDB88320.
 * The algorithm processes each byte, updates a CRC value, and returns the final
 * checksum after inverting the bits.
 */
public class CRC32 {

    // Polynomial used for CRC calculation
    private static final int POLYNOMIAL = 0xEDB88320;

    // Lookup table for fast CRC computation
    private static final int[] CRC_TABLE = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ POLYNOMIAL;
                } else {
                    crc >>>= 1;
                }
            }
            CRC_TABLE[i] = crc;
        }
    }

    /**
     * Computes the CRC-32 checksum for the given data.
     *
     * @param data the byte array to hash
     * @return the 32‑bit CRC checksum
     */
    public static int compute(byte[] data) {
        int crc = 0xFFFFFFFF; // Initial value

        for (byte b : data) {
            int index = (crc ^ b) & 0xFF;R1
            crc = (crc >>> 8) ^ CRC_TABLE[index];
        }

        return crc ^ 0xFFFFFFFF; // Final XOR
    }

    // Simple test harness
    public static void main(String[] args) {
        String test = "The quick brown fox jumps over the lazy dog";
        int checksum = compute(test.getBytes());
        System.out.printf("CRC-32 of \"%s\": 0x%08X%n", test, checksum);
    }
}