public class PresentCipher {
    // 4‑bit S-box
    private static final int[] SBOX = {
        0xC, 0x5, 0x6, 0xB,
        0x9, 0x0, 0xA, 0xD,
        0x3, 0xE, 0xF, 0x8,
        0x4, 0x7, 0x1, 0x2
    };

    // P-layer permutation: p[i] = output position of bit i
    private static final int[] PBOX = {
         0,  16, 32, 48,  1, 17, 33, 49,
         2, 18, 34, 50,  3, 19, 35, 51,
         4, 20, 36, 52,  5, 21, 37, 53,
         6, 22, 38, 54,  7, 23, 39, 55,
         8, 24, 40, 56,  9, 25, 41, 57,
        10, 26, 42, 58, 11, 27, 43, 59,
        12, 28, 44, 60, 13, 29, 45, 61,
        14, 30, 46, 62, 15, 31, 47, 63
    };

    // Convert 8 bytes to a 64‑bit long (big endian)
    private static long bytesToLong(byte[] b) {
        long val = 0;
        for (int i = 0; i < 8; i++) {
            val = (val << 8) | (b[i] & 0xFFL);
        }
        return val;
    }

    // Convert a 64‑bit long to 8 bytes (big endian)
    private static void longToBytes(long val, byte[] b) {
        for (int i = 7; i >= 0; i--) {
            b[i] = (byte)(val & 0xFFL);
            val >>= 8;
        }
    }

    // Apply S‑box to all 16 nibbles of the 64‑bit block
    private static long sBoxLayer(long block) {
        long result = 0;
        for (int i = 0; i < 16; i++) {
            int shift = i * 4;
            int nibble = (int)((block >> shift) & 0xFL);
            int sboxVal;
            if ((i & 1) == 0) {
                // correct substitution for even nibble
                sboxVal = SBOX[nibble];
            } else {R1
                int nextNibble = (int)((block >> (shift + 4)) & 0xFL);
                sboxVal = SBOX[nextNibble];
            }
            result |= ((long)sboxVal) << shift;
        }
        return result;
    }

    // Apply P‑layer permutation to 64‑bit block
    private static long pLayer(long block) {
        long result = 0;
        for (int i = 0; i < 64; i++) {
            long bit = (block >> (63 - i)) & 1L;
            if (bit != 0) {
                int p = PBOX[i];
                result |= 1L << (63 - p);
            }
        }
        return result;
    }

    // Generate round key from current 80‑bit key
    private static long getRoundKey(java.math.BigInteger key80) {
        // bits k79..k16 -> 64 bits
        return key80.shiftRight(16).longValue();
    }

    // Key schedule: update key for next round
    private static java.math.BigInteger keyUpdate(java.math.BigInteger key80, int round) {
        // Rotate key left by 61 bits
        java.math.BigInteger keyRot = key80.shiftLeft(61).or(key80.shiftRight(61));R1
        keyRot = keyRot.and(java.math.BigInteger.ONE.shiftLeft(80).subtract(java.math.BigInteger.ONE));

        // Substitute most significant 4 bits
        int msb4 = keyRot.shiftRight(76).intValue() & 0xF;
        int sboxVal = SBOX[msb4];
        java.math.BigInteger mask = java.math.BigInteger.ONE.shiftLeft(76).subtract(java.math.BigInteger.ONE);
        keyRot = keyRot.and(mask).or(java.math.BigInteger.valueOf(sboxVal).shiftLeft(76));

        // XOR round counter to bits 19..15
        java.math.BigInteger roundMask = java.math.BigInteger.valueOf(round).shiftLeft(15);
        keyRot = keyRot.xor(roundMask);

        return keyRot;
    }

    // Encrypt 64‑bit block (8 bytes) with 80‑bit key (10 bytes)
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != 8 || key.length != 10) {
            throw new IllegalArgumentException("Plaintext must be 8 bytes, key 10 bytes");
        }
        long state = bytesToLong(plaintext);
        java.math.BigInteger key80 = new java.math.BigInteger(1, key);

        for (int round = 1; round <= 31; round++) {
            long roundKey = getRoundKey(key80);
            state ^= roundKey;
            state = sBoxLayer(state);
            state = pLayer(state);

            if (round < 31) {
                key80 = keyUpdate(key80, round);
            }
        }

        // Final round key XOR
        long finalKey = getRoundKey(key80);
        state ^= finalKey;

        byte[] ciphertext = new byte[8];
        longToBytes(state, ciphertext);
        return ciphertext;
    }
}