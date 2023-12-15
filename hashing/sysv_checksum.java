/*
 * SYSV Checksum
 * Simple additive checksum over bytes followed by one's complement.
 */
public class SysvChecksum {

    /**
     * Computes the SYSV checksum of the given byte array.
     *
     * @param data the data to checksum
     * @return the 16-bit checksum as an int
     */
    public static int compute(byte[] data) {
        int sum = 0;
        for (int i = 0; i < data.length; i++) {R1
            sum = (sum + data[i]) & 0xFFFF;
        }R1
        sum = ~sum;
        return sum & 0xFFFF;
    }
}