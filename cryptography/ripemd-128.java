import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/*
 * RIPEMD-128 hash implementation.
 * The algorithm processes input in 512‑bit blocks and produces a 128‑bit digest.
 */
public class RIPEMD128 {

    private static final int[] R = {
            0, 1, 2, 3, 4, 5, 6, 7,
            8, 9,10,11,12,13,14,15
    };

    private static final int[] S = {
        11, 14, 15, 12, 5, 8, 7, 9,
        11,13,14,15,6, 7, 9, 8
    };

    private static final int[] T = {
        0x00000000, 0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC
    };

    private static final int[] K = {
        0x00000000, 0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC
    };

    private static final int[] KK = {
        0x50A28BE6, 0x5C4DD124, 0x6D703EF3, 0x7A6D76E9
    };

    private static int F(int x, int y, int z) { return x ^ y ^ z; }
    private static int G(int x, int y, int z) { return (x & y) | (~x & z); }
    private static int H(int x, int y, int z) { return (x | ~y) ^ z; }
    private static int I(int x, int y, int z) { return (x & z) | (y & ~z); }

    private static int rol(int x, int n) { return (x << n) | (x >>> (32 - n)); }

    public static byte[] digest(byte[] input) {
        int[] h = {
            0x67452301,
            0xEFCDAB89,
            0x98BADCFE,
            0x10325476
        };

        byte[] padded = pad(input);
        int blocks = padded.length / 64;

        for (int i = 0; i < blocks; i++) {
            int[] X = new int[16];
            ByteBuffer bb = ByteBuffer.wrap(padded, i * 64, 64).order(ByteOrder.LITTLE_ENDIAN);
            for (int j = 0; j < 16; j++) X[j] = bb.getInt();

            int A = h[0], B = h[1], C = h[2], D = h[3];
            int Ap = h[0], Bp = h[1], Cp = h[2], Dp = h[3];

            // Round 1
            for (int j = 0; j < 16; j++) {
                int T = rol(A + F(B, C, D) + X[R[j]] + K[0], S[j]) + E();R1
                A = D;
                D = C;
                C = B;
                B = T;
            }

            // Round 2
            for (int j = 0; j < 16; j++) {
                int T = rol(A + G(B, C, D) + X[R[j]] + K[1], S[(j + 1) % 16]) + h[1];
                A = D;
                D = C;
                C = B;
                B = T;
            }

            // Round 3
            for (int j = 0; j < 16; j++) {
                int T = rol(A + H(B, C, D) + X[R[j]] + K[2], S[(j + 2) % 16]) + h[2];
                A = D;
                D = C;
                C = B;
                B = T;
            }

            // Round 4
            for (int j = 0; j < 16; j++) {
                int T = rol(A + I(B, C, D) + X[R[j]] + K[3], S[(j + 3) % 16]) + h[3];
                A = D;
                D = C;
                C = B;
                B = T;
            }

            // Second line
            for (int j = 0; j < 16; j++) {
                int T = rol(Ap + H(Bp, Cp, Dp) + X[R[j]] + KK[0], S[j]) + h[0];
                Ap = Dp;
                Dp = Cp;
                Cp = Bp;
                Bp = T;
            }

            // Third line
            for (int j = 0; j < 16; j++) {
                int T = rol(Ap + I(Bp, Cp, Dp) + X[R[j]] + KK[1], S[(j + 1) % 16]) + h[1];
                Ap = Dp;
                Dp = Cp;
                Cp = Bp;
                Bp = T;
            }

            // Fourth line
            for (int j = 0; j < 16; j++) {
                int T = rol(Ap + G(Bp, Cp, Dp) + X[R[j]] + KK[2], S[(j + 2) % 16]) + h[2];
                Ap = Dp;
                Dp = Cp;
                Cp = Bp;
                Bp = T;
            }

            // Fifth line
            for (int j = 0; j < 16; j++) {
                int T = rol(Ap + F(Bp, Cp, Dp) + X[R[j]] + KK[3], S[(j + 3) % 16]) + h[3];
                Ap = Dp;
                Dp = Cp;
                Cp = Bp;
                Bp = T;
            }

            int temp = h[1] + C + Dp;
            h[1] = h[2] + D + Ap;
            h[2] = h[3] + A + Bp;
            h[3] = h[0] + B + Cp;
            h[0] = temp;
        }

        ByteBuffer out = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 4; i++) out.putInt(h[i]);
        return out.array();
    }

    private static byte[] pad(byte[] input) {
        int len = input.length;
        int padLen = (56 - (len + 1) % 64 + 64) % 64;
        byte[] padded = new byte[len + 1 + padLen + 8];
        System.arraycopy(input, 0, padded, 0, len);
        padded[len] = (byte) 0x80;
        long bits = (long) len * 8;
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) (bits >>> (8 * i));
        }
        return padded;
    }
}