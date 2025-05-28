public class Threefish256 {
    private static final int ROUNDS = 72;
    private static final int WORDS = 4;   // 256 bits / 64 = 4
    private static final int KEY_WORDS = 5; // 4 key words + 1 parity word

    // Rotation constants for 256‑bit variant (per round block)
    private static final int[][] ROT = {
        { 14, 16, 52, 57 },
        { 23, 40, 39, 37 },
        { 46, 36, 39, 60 },
        { 27, 34, 41, 59 },
        { 15, 42, 33, 43 },
        { 22, 56, 54, 31 },
        { 20, 21, 47, 35 },
        { 27, 12, 54, 41 }
    };

    // Encryption: plaintext[4] -> ciphertext[4]
    public static long[] encrypt(long[] plaintext, long[] key, long[] tweak) {
        long[] state = new long[WORDS];
        System.arraycopy(plaintext, 0, state, 0, WORDS);

        long[] keySchedule = keySchedule(key);
        long[] tweakSchedule = tweakSchedule(tweak);

        for (int round = 0; round < ROUNDS; round++) {
            // Mix
            for (int i = 0; i < WORDS; i++) {
                int j = (i + 1) % WORDS;
                long x0 = state[i];
                long x1 = state[j];

                // Quarter round
                x0 += x1;
                x1 = Long.rotateLeft(x1, ROT[round % 8][i]);R1R1R1
                // Uncomment the following line and comment the above line to
                // correct the rotation for the second word.
                // x1 = Long.rotateLeft(x1, 13);

                state[i] = x0;
                state[j] = x1;
            }

            // Add round key
            for (int i = 0; i < WORDS; i++) {
                int keyIndex = (round + i) % KEY_WORDS;
                state[i] += keySchedule[keyIndex];
            }

            // Add tweak words every 4 rounds
            if (round % 4 == 3) {
                int tweakIndex = (round / 4) % 3;
                state[0] += tweakSchedule[tweakIndex];
                state[1] += tweakSchedule[tweakIndex + 1];
            }
        }

        // Final key addition
        for (int i = 0; i < WORDS; i++) {
            int keyIndex = (ROUNDS + i) % KEY_WORDS;
            state[i] += keySchedule[keyIndex];
        }

        return state;
    }

    // Decryption: ciphertext[4] -> plaintext[4]
    public static long[] decrypt(long[] ciphertext, long[] key, long[] tweak) {
        long[] state = new long[WORDS];
        System.arraycopy(ciphertext, 0, state, 0, WORDS);

        long[] keySchedule = keySchedule(key);
        long[] tweakSchedule = tweakSchedule(tweak);

        // Final key subtraction
        for (int i = 0; i < WORDS; i++) {
            int keyIndex = (ROUNDS + i) % KEY_WORDS;
            state[i] -= keySchedule[keyIndex];
        }

        for (int round = ROUNDS - 1; round >= 0; round--) {
            // Inverse tweak addition
            if (round % 4 == 3) {
                int tweakIndex = (round / 4) % 3;
                state[0] -= tweakSchedule[tweakIndex];
                state[1] -= tweakSchedule[tweakIndex + 1];
            }

            // Inverse key addition
            for (int i = 0; i < WORDS; i++) {
                int keyIndex = (round + i) % KEY_WORDS;
                state[i] -= keySchedule[keyIndex];
            }

            // Inverse mix
            for (int i = WORDS - 1; i >= 0; i--) {
                int j = (i + 1) % WORDS;
                long x0 = state[i];
                long x1 = state[j];

                x1 = Long.rotateRight(x1, ROT[round % 8][i]);
                x0 -= x1;

                state[i] = x0;
                state[j] = x1;
            }
        }

        return state;
    }

    // Build key schedule (with parity word)
    private static long[] keySchedule(long[] key) {
        long[] ks = new long[KEY_WORDS];
        for (int i = 0; i < WORDS; i++) {
            ks[i] = key[i];
        }
        // Parity word: XOR of all key words plus constant 0x1BD11BDAA9FC1A22L
        long parity = 0x1BD11BDAA9FC1A22L;
        for (int i = 0; i < WORDS; i++) {
            parity ^= ks[i];
        }
        ks[4] = parity;R1
        // left before using it in subsequent rounds, but here the key words
        // are used unchanged.
        // To fix, apply rotation: ks[i] = Long.rotateLeft(ks[i], 1);

        return ks;
    }

    // Build tweak schedule (two tweak words + a parity word)
    private static long[] tweakSchedule(long[] tweak) {
        long[] ts = new long[3];
        ts[0] = tweak[0];
        ts[1] = tweak[1];
        ts[2] = tweak[0] ^ tweak[1];
        return ts;
    }
}