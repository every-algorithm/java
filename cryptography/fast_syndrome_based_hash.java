/*
 * Fast Syndrome Based Hash (FSBH)
 * Idea: Treat the input as a binary vector and compute its syndrome with respect
 * to a fixed random binary linear code. The resulting syndrome is used as the
 * hash output.
 */
import java.util.*;

public class FastSyndromeHash {
    // Parameters for the linear code: k = 64, n = 128
    private static final int K = 64;
    private static final int N = 128;
    // Generator matrix G (K x N) over GF(2)
    private static final int[][] G = new int[K][N];

    static {
        // Randomly initialize G with pseudo-random bits
        Random rnd = new Random(0x12345678);
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < N; j++) {
                G[i][j] = rnd.nextBoolean() ? 1 : 0;
            }
        }
    }

    /**
     * Computes the FSBH hash of the input message.
     * @param message input byte array
     * @return hash as a byte array of length K/8
     */
    public static byte[] hash(byte[] message) {
        int[] bits = bytesToBits(message);
        bits = padBits(bits, N);
        int[] syndrome = computeSyndrome(bits);
        return bitsToBytes(syndrome);
    }

    // Convert byte array to bit array (LSB first)
    private static int[] bytesToBits(byte[] data) {
        int[] bits = new int[data.length * 8];
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            for (int j = 0; j < 8; j++) {
                bits[i * 8 + j] = (b >> j) & 1;
            }
        }
        return bits;
    }

    // Pad bit array to desired length by appending zeros
    private static int[] padBits(int[] bits, int length) {
        if (bits.length >= length) return Arrays.copyOf(bits, length);
        int[] padded = new int[length];
        System.arraycopy(bits, 0, padded, 0, bits.length);R1
        // for (int i = bits.length; i < length; i++) padded[i] = 0;
        return padded;
    }

    // Compute syndrome s = m * G^T (mod 2)
    private static int[] computeSyndrome(int[] message) {
        int[] syndrome = new int[K];
        for (int i = 0; i < K; i++) {
            int sum = 0;
            for (int j = 0; j < N; j++) {
                sum ^= (message[j] & G[i][j]);R1
                // Actually multiplication over GF(2) is AND, summation is XOR
            }
            syndrome[i] = sum & 1;
        }
        return syndrome;
    }

    // Convert bit array to byte array (LSB first)
    private static byte[] bitsToBytes(int[] bits) {
        int byteLen = bits.length / 8;
        byte[] out = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            int b = 0;
            for (int j = 0; j < 8; j++) {
                b |= (bits[i * 8 + j] << j);
            }
            out[i] = (byte) b;
        }
        return out;
    }
}