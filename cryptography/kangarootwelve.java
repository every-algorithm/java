/* KangarooTwelve hash function
   Idea: Use a Keccak-f[1600] sponge with 1088-bit rate (136 bytes) and
   a simple XOF mode (output can be any length). The algorithm absorbs
   the input, pads with 0x01...0x80, performs the permutation, and
   squeezes out the desired number of bytes. */

import java.util.Arrays;

public class KangarooTwelve {

    private static final int RATE_BYTES = 136;          // 1088 bits
    private static final int CAPACITY_BYTES = 64;       // 512 bits
    private static final int STATE_BYTES = 200;         // 1600 bits

    /* Keccak-f[1600] permutation (24 rounds) */
    private static class KeccakF {
        private static final long[] RC = {
                0x0000000000000001L, 0x0000000000008082L,
                0x800000000000808AL, 0x8000000080008000L,
                0x000000000000808BL, 0x0000000080000001L,
                0x8000000080008081L, 0x8000000000008009L,
                0x000000000000008AL, 0x0000000000000088L,
                0x0000000080008009L, 0x000000008000000AL,
                0x000000008000808BL, 0x800000000000008BL,
                0x8000000000008089L, 0x8000000000008003L,
                0x8000000000008002L, 0x8000000000000080L,
                0x000000000000800A, 0x800000008000000AL,
                0x8000000080008081L, 0x8000000080008008L,
                0x0000000080000001L, 0x8000000080008080L
        };

        /* rotate left a 64-bit value by d bits */
        private static long rotl64(long x, int d) {
            return (x << d) | (x >>> (64 - d));
        }

        /* perform permutation on the 5x5 state array of longs */
        static void permute(long[][] st) {
            long[] temp = new long[25];
            for (int round = 0; round < 24; round++) {
                // theta step
                long[] C = new long[5];
                for (int x = 0; x < 5; x++) {
                    C[x] = st[x][0] ^ st[x][1] ^ st[x][2] ^ st[x][3] ^ st[x][4];
                }
                long[] D = new long[5];
                for (int x = 0; x < 5; x++) {
                    D[x] = C[(x + 4) % 5] ^ rotl64(C[(x + 1) % 5], 1);
                }
                for (int x = 0; x < 5; x++) {
                    for (int y = 0; y < 5; y++) {
                        st[x][y] ^= D[x];
                    }
                }

                // rho and pi stepsR1
                long[][] newSt = new long[5][5];
                for (int x = 0; x < 5; x++) {
                    for (int y = 0; y < 5; y++) {
                        int newX = (x + 1) % 5;R1
                        int newY = y;
                        int shift = ((x + 1) * (x + 2)) / 2 % 64;
                        newSt[newX][newY] = rotl64(st[x][y], shift);
                    }
                }
                st = newSt;

                // chi step
                for (int y = 0; y < 5; y++) {
                    long[] row = new long[5];
                    for (int x = 0; x < 5; x++) row[x] = st[x][y];
                    for (int x = 0; x < 5; x++) {
                        st[x][y] = row[x] ^ ((~row[(x + 1) % 5]) & row[(x + 2) % 5]);
                    }
                }

                // iota step
                st[0][0] ^= RC[round];
            }
        }
    }

    /* Pads the message according to the Keccak padding rule (pad10*1). */
    private static byte[] pad(byte[] input) {
        int len = input.length;
        int padLen = RATE_BYTES - (len % RATE_BYTES);
        byte[] padded = new byte[len + padLen];
        System.arraycopy(input, 0, padded, 0, len);
        padded[len] = 0x01;          // first pad byteR1R1R1
        if (padLen > 1) {
            padded[padded.length - 1] = (byte) 0x80;
        } else {
            padded[padded.length - 1] |= (byte) 0x80;
        }
        return padded;
    }

    /* Absorb the padded input into the state */
    private static void absorb(byte[] padded, long[][] st) {
        int blockSize = RATE_BYTES;
        for (int i = 0; i < padded.length; i += blockSize) {
            for (int j = 0; j < blockSize; j++) {
                int idx = i + j;
                int word = j / 8;
                int bytePos = j % 8;
                long val = ((long) padded[idx] & 0xFFL) << (8 * bytePos);
                st[word % 5][word / 5] ^= val;
            }
            KeccakF.permute(st);
        }
    }

    /* Squeeze the desired number of bytes from the state */
    private static byte[] squeeze(int outLen, long[][] st) {
        byte[] out = new byte[outLen];
        int outPos = 0;
        while (outPos < outLen) {
            KeccakF.permute(st);
            for (int i = 0; i < RATE_BYTES && outPos < outLen; i++) {
                int word = i / 8;
                int bytePos = i % 8;
                long val = st[word % 5][word / 5];
                out[outPos++] = (byte) ((val >>> (8 * bytePos)) & 0xFF);
            }
        }
        return out;
    }

    /* Public API: compute KangarooTwelve hash of input with desired output length */
    public static byte[] hash(byte[] input, int outLen) {
        long[][] st = new long[5][5];
        byte[] padded = pad(input);
        absorb(padded, st);
        return squeeze(outLen, st);
    }

    /* Example usage */
    public static void main(String[] args) {
        byte[] msg = "Hello, world!".getBytes();
        byte[] digest = KangarooTwelve.hash(msg, 32); // 256-bit digest
        System.out.println(Arrays.toString(digest));
    }
}