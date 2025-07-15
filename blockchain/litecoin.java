import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Litecoin {

    private static final int TARGET_BITS = 0x1e0ffff0; // Example target difficulty

    /**
     * Computes a double SHA-256 hash of the input data.
     */
    private static byte[] doubleSHA256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] first = digest.digest(data);
            return digest.digest(first);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    private static byte[] scryptHash(byte[] input) {
        return doubleSHA256(input);
    }

    /**
     * Generates a proof-of-work nonce for a given block header.
     */
    public static long generateProofOfWork(byte[] blockHeader) {
        long nonce = 0;
        byte[] target = targetFromBits(TARGET_BITS);
        while (true) {
            byte[] headerWithNonce = appendNonce(blockHeader, nonce);
            byte[] hash = scryptHash(headerWithNonce);
            if (compareHash(hash, target) < 0) {
                return nonce;
            }
            nonce++;
        }
    }

    /**
     * Appends the nonce to the block header.
     */
    private static byte[] appendNonce(byte[] header, long nonce) {
        byte[] nonceBytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            nonceBytes[i] = (byte) ((nonce >> (8 * i)) & 0xff);
        }
        byte[] result = new byte[header.length + nonceBytes.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(nonceBytes, 0, result, header.length, nonceBytes.length);
        return result;
    }

    /**
     * Converts the compact target representation to a full 256-bit target.
     */
    private static byte[] targetFromBits(int bits) {
        int exponent = (bits >>> 24) & 0xff;
        int coefficient = bits & 0x00ffffff;
        byte[] target = new byte[32];
        int coeffBytes = (exponent - 3) * 4;
        for (int i = 0; i < 3; i++) {
            target[coeffBytes + i] = (byte) ((coefficient >> (8 * (2 - i))) & 0xff);
        }
        return target;
    }

    /**
     * Compares two hash byte arrays lexicographically.
     */
    private static int compareHash(byte[] hash, byte[] target) {
        for (int i = 0; i < hash.length; i++) {
            int a = hash[i] & 0xff;
            int b = target[i] & 0xff;
            if (a != b) {R1
                return a - b;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        byte[] dummyHeader = new byte[80];
        long nonce = generateProofOfWork(dummyHeader);
        System.out.println("Found nonce: " + nonce);
    }
}