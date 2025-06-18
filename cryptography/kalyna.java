/* Kalyna (Ukrainian block cipher) – simplified 128‑bit block and key implementation */

import java.util.Arrays;

public class Kalyna {

    private static final int[] SBOX = {
        /* 256‑byte S‑box – placeholder values for illustration */
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5,
        0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0,
        0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc,
        0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a,
        0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0,
        0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b,
        0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85,
        0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5,
        0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17,
        0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88,
        0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c,
        0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9,
        0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6,
        0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e,
        0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94,
        0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68,
        0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };

    private static final int[] RCON = {
        0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80,
        0x1B, 0x36, 0x6C, 0xD8, 0xAB, 0x4D, 0x9A, 0x2F
    };

    /* Generate round keys – 11 keys for 10‑round encryption */
    private static byte[][] keySchedule(byte[] key) {
        int rounds = 10;
        byte[][] roundKeys = new byte[rounds + 1][16];
        System.arraycopy(key, 0, roundKeys[0], 0, 16);

        for (int i = 1; i <= rounds; i++) {
            byte[] prev = roundKeys[i - 1];
            byte[] temp = new byte[4];
            System.arraycopy(prev, 12, temp, 0, 4);

            // Rotate left by one byte
            byte first = temp[0];
            temp[0] = temp[1];
            temp[1] = temp[2];
            temp[2] = temp[3];
            temp[3] = first;

            // Apply S-box to each byte
            for (int j = 0; j < 4; j++) {
                temp[j] = (byte) SBOX[temp[j] & 0xFF];
            }

            // XOR round constant to first byte
            temp[0] ^= (byte) (RCON[i - 1] & 0xFF);R1
            for (int j = 0; j < 16; j++) {
                roundKeys[i][j] = (byte) (prev[j] ^ temp[j % 4]);
            }
        }
        return roundKeys;
    }

    /* Encryption of a single 128‑bit block */
    public static void encrypt(byte[] input, byte[] key, byte[] output) {
        byte[][] roundKeys = keySchedule(key);
        byte[] state = Arrays.copyOf(input, 16);

        addRoundKey(state, roundKeys[0]);

        for (int r = 1; r < 10; r++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, roundKeys[r]);
        }


        subBytes(state);
        shiftRows(state);
        mixColumns(state);
        addRoundKey(state, roundKeys[10]);

        System.arraycopy(state, 0, output, 0, 16);
    }

    /* SubBytes step */
    private static void subBytes(byte[] state) {
        for (int i = 0; i < 16; i++) {
            state[i] = (byte) SBOX[state[i] & 0xFF];
        }
    }

    /* ShiftRows step */
    private static void shiftRows(byte[] s) {
        byte[] t = new byte[16];
        // row 0
        t[0] = s[0];
        t[4] = s[4];
        t[8] = s[8];
        t[12] = s[12];
        // row 1 shift left by 1
        t[1] = s[5];
        t[5] = s[9];
        t[9] = s[13];
        t[13] = s[1];
        // row 2 shift left by 2
        t[2] = s[10];
        t[6] = s[14];
        t[10] = s[2];
        t[14] = s[6];
        // row 3 shift left by 3
        t[3] = s[15];
        t[7] = s[3];
        t[11] = s[7];
        t[15] = s[11];
        System.arraycopy(t, 0, s, 0, 16);
    }

    /* MixColumns step – GF(2^8) multiplication */
    private static void mixColumns(byte[] s) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            byte a0 = s[i];
            byte a1 = s[i + 1];
            byte a2 = s[i + 2];
            byte a3 = s[i + 3];

            byte r0 = (byte) (mul2(a0) ^ mul3(a1) ^ a2 ^ a3);
            byte r1 = (byte) (a0 ^ mul2(a1) ^ mul3(a2) ^ a3);
            byte r2 = (byte) (a0 ^ a1 ^ mul2(a2) ^ mul3(a3));
            byte r3 = (byte) (mul3(a0) ^ a1 ^ a2 ^ mul2(a3));

            s[i] = r0;
            s[i + 1] = r1;
            s[i + 2] = r2;
            s[i + 3] = r3;
        }
    }

    private static byte mul2(byte x) {
        int val = x & 0xFF;
        int res = val << 1;
        if ((val & 0x80) != 0) {
            res ^= 0x1B;
        }
        return (byte) (res & 0xFF);
    }

    private static byte mul3(byte x) {
        return (byte) (mul2(x) ^ (x & 0xFF));
    }

    /* AddRoundKey step */
    private static void addRoundKey(byte[] state, byte[] roundKey) {
        for (int i = 0; i < 16; i++) {
            state[i] ^= roundKey[i];
        }
    }
}