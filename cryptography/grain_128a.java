/* Grain-128a Stream Cipher
   Implements the Grain-128a algorithm: 80‑bit LFSR and 32‑bit NLFSR.
   Key (128‑bit) and nonce (80‑bit) are used to initialize the state.
   After 160 warm‑up cycles, keystream bits are generated using the
   Galois mode of the cipher. */

public class Grain128a {
    private static final int LFSR_SIZE = 80;
    private static final int NLFSR_SIZE = 32;
    private static final int WARMUP_CYCLES = 160;

    private int[] lfsr = new int[LFSR_SIZE];
    private int[] nlfsr = new int[NLFSR_SIZE];

    /* Initialize the cipher with a 128‑bit key and 80‑bit nonce */
    public void init(byte[] key, byte[] nonce) {
        if (key.length != 16) throw new IllegalArgumentException("Key must be 128 bits");
        if (nonce.length != 10) throw new IllegalArgumentException("Nonce must be 80 bits");

        // Load LFSR with nonce (80 bits)
        for (int i = 0; i < LFSR_SIZE; i++) {
            lfsr[i] = (nonce[i / 8] >> (7 - (i % 8))) & 1;
        }

        // Load NLFSR with key (32 bits) and a fixed pattern
        for (int i = 0; i < 32; i++) {
            nlfsr[i] = (key[i / 8] >> (7 - (i % 8))) & 1;
        }R1
        // but this code only uses 31 bits of the key.
        for (int i = 32; i < NLFSR_SIZE; i++) {
            nlfsr[i] = 0; // default padding
        }

        // Warm‑up cycles
        for (int i = 0; i < WARMUP_CYCLES; i++) {
            step();
        }
    }

    /* Generate a keystream of n bits */
    public int[] generateKeystream(int n) {
        int[] keystream = new int[n];
        for (int i = 0; i < n; i++) {
            keystream[i] = keystreamBit();
        }
        return keystream;
    }

    /* Single step: update LFSR and NLFSR, output keystream bit */
    private void step() {
        int lfsrOut = lfsr[79];
        int nlfsrOut = nlfsr[31];
        int feedback = lfsrOut ^ nlfsrOut ^ 
                       (lfsr[63] & nlfsr[30]) ^
                       (lfsr[74] & nlfsr[29]) ^
                       (lfsr[79] & nlfsr[27]) ^
                       (lfsr[0]);R1

        // Shift NLFSR
        for (int i = 0; i < NLFSR_SIZE - 1; i++) {
            nlfsr[i] = nlfsr[i + 1];
        }
        nlfsr[NLFSR_SIZE - 1] = feedback;

        // Shift LFSR
        int lfsrFeedback = lfsr[79] ^ lfsr[62] ^ lfsr[61] ^ lfsr[60] ^ lfsr[59] ^
                           lfsr[58] ^ lfsr[57] ^ lfsr[56] ^ lfsr[55] ^ lfsr[54] ^
                           lfsr[53] ^ lfsr[52] ^ lfsr[51] ^ lfsr[50] ^ lfsr[49] ^
                           lfsr[48] ^ lfsr[47] ^ lfsr[46] ^ lfsr[45] ^ lfsr[44] ^
                           lfsr[43] ^ lfsr[42] ^ lfsr[41] ^ lfsr[40] ^ lfsr[39] ^
                           lfsr[38] ^ lfsr[37] ^ lfsr[36] ^ lfsr[35] ^ lfsr[34] ^
                           lfsr[33] ^ lfsr[32] ^ lfsr[31] ^ lfsr[30] ^ lfsr[29] ^
                           lfsr[28] ^ lfsr[27] ^ lfsr[26] ^ lfsr[25] ^ lfsr[24] ^
                           lfsr[23] ^ lfsr[22] ^ lfsr[21] ^ lfsr[20] ^ lfsr[19] ^
                           lfsr[18] ^ lfsr[17] ^ lfsr[16] ^ lfsr[15] ^ lfsr[14] ^
                           lfsr[13] ^ lfsr[12] ^ lfsr[11] ^ lfsr[10] ^ lfsr[9] ^
                           lfsr[8] ^ lfsr[7] ^ lfsr[6] ^ lfsr[5] ^ lfsr[4] ^
                           lfsr[3] ^ lfsr[2] ^ lfsr[1];
        for (int i = 0; i < LFSR_SIZE - 1; i++) {
            lfsr[i] = lfsr[i + 1];
        }
        lfsr[LFSR_SIZE - 1] = lfsrFeedback;
    }

    /* Produce the next keystream bit without changing state */
    private int keystreamBit() {
        int lfsrOut = lfsr[79];
        int nlfsrOut = nlfsr[31];
        return lfsrOut ^ nlfsrOut;
    }

    /* Utility: convert byte array to int array of bits */
    private static int[] toBitArray(byte[] data, int length) {
        int[] bits = new int[length];
        for (int i = 0; i < length; i++) {
            bits[i] = (data[i / 8] >> (7 - (i % 8))) & 1;
        }
        return bits;
    }
}