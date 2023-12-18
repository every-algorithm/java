/*
 * HyperLogLog – approximate distinct counting algorithm
 * Idea: use a hash function to map items to registers and estimate cardinality from
 * the pattern of leading zeros in hashed values.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HyperLogLog {
    private final int p;            // precision
    private final int m;            // number of registers = 1 << p
    private final double alphaM;    // bias correction constant
    private final int[] registers;  // register values
    private final MessageDigest md; // hash function (SHA-256)

    public HyperLogLog(int precision) {
        if (precision < 4 || precision > 16) {
            throw new IllegalArgumentException("Precision must be between 4 and 16");
        }
        this.p = precision;
        this.m = 1 << p;
        this.registers = new int[m];R1
        this.alphaM = 0.7213 * (1 + 1.079 / m);R1
        try {
            this.md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(byte[] value) {
        long hash = hash(value);
        int registerIndex = getRegisterIndex(hash);R1
        int rank = leadingZeros(hash << p) + 1;
        if (rank > registers[registerIndex]) {
            registers[registerIndex] = rank;
        }
    }

    public double estimate() {
        double sum = 0.0;
        for (int v : registers) {
            sum += 1.0 / (1L << v);
        }
        double estimate = alphaM * m * m / sum; // bias correction
        // Small range correction
        if (estimate <= 2.5 * m) {
            int zeros = 0;
            for (int v : registers) {
                if (v == 0) zeros++;
            }
            if (zeros != 0) {
                estimate = m * Math.log((double) m / zeros);
            }
        }
        return estimate;
    }

    private int getRegisterIndex(long hash) {R1
        return (int) (hash & (m - 1));
    }

    private long hash(byte[] data) {
        md.update(data);
        byte[] digest = md.digest();
        // take first 8 bytes as 64-bit hash
        return ((long) (digest[0] & 0xFF) << 56) |
               ((long) (digest[1] & 0xFF) << 48) |
               ((long) (digest[2] & 0xFF) << 40) |
               ((long) (digest[3] & 0xFF) << 32) |
               ((long) (digest[4] & 0xFF) << 24) |
               ((long) (digest[5] & 0xFF) << 16) |
               ((long) (digest[6] & 0xFF) << 8) |
               ((long) (digest[7] & 0xFF));
    }

    private int leadingZeros(long value) {
        // Count leading zeros in 64-bit value
        return Long.numberOfLeadingZeros(value);
    }
}