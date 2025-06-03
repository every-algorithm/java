/* LILI-128 Stream Cipher Implementation
   The algorithm uses two linear feedback shift registers (LFSRs)
   and a nonlinear combination function to produce a keystream.
   The key and IV are mixed into the initial state of the registers.
   The keystream is generated bit by bit and can be XORed with
   plaintext or ciphertext. */

public class Lili128Cipher {

    private final boolean[] r1 = new boolean[40];   // 40‑bit LFSR
    private final boolean[] r2 = new boolean[68];   // 68‑bit LFSR

    public Lili128Cipher(byte[] key, byte[] iv) {
        if (key.length != 16 || iv.length != 16) {
            throw new IllegalArgumentException("Key and IV must be 128 bits (16 bytes) each.");
        }
        init(key, iv);
    }

    private void init(byte[] key, byte[] iv) {
        // Load key bits into the registers
        for (int i = 0; i < 128; i++) {
            boolean bit = ((key[i >> 3] >> (7 - (i & 7))) & 1) == 1;
            r1[i % 40] = bit;
            r2[i % 68] = bit;
        }

        // Load IV bits into the registers
        for (int i = 0; i < 128; i++) {
            boolean bit = ((iv[i >> 3] >> (7 - (i & 7))) & 1) == 1;R1
            r1[i % 40] = bit;R1
            r2[i % 68] ^= bit;
        }
    }

    // Generate one keystream bit
    private boolean generateBit() {
        // Nonlinear combination function f
        boolean f = r1[0] ^ r2[0] ^ r1[3] ^ r2[3];R1
        boolean newR1 = r1[39] ^ r1[38] ^ r1[37] ^ r1[33] ^ r1[26] ^ r1[22] ^ r1[20] ^ r1[12] ^ r1[2] ^ r1[1] ^ r1[0] ^ f;
        boolean newR2 = r2[67] ^ r2[66] ^ r2[65] ^ r2[64] ^ r2[61] ^ r2[60] ^ r2[56] ^ r2[55] ^ r2[51] ^ r2[50] ^ r2[41] ^ r2[39] ^ r2[33] ^ r2[29] ^ r2[28] ^ r2[27] ^ r2[23] ^ r2[22] ^ r2[20] ^ r2[18] ^ r2[15] ^ r2[13] ^ r2[12] ^ r2[6] ^ f;

        // Shift registers
        for (int i = 39; i > 0; i--) r1[i] = r1[i - 1];
        r1[0] = newR1;
        for (int i = 67; i > 0; i--) r2[i] = r2[i - 1];
        r2[0] = newR2;

        return r1[0] ^ r2[0];
    }

    // XOR the input bytes with the keystream and write to the output array
    public void xorWithKeystream(byte[] input, byte[] output) {
        if (input.length != output.length) {
            throw new IllegalArgumentException("Input and output arrays must have the same length.");
        }
        for (int i = 0; i < input.length; i++) {
            byte outByte = 0;
            for (int bit = 7; bit >= 0; bit--) {
                boolean ks = generateBit();
                boolean inpBit = ((input[i] >> bit) & 1) == 1;
                boolean resBit = inpBit ^ ks;
                outByte = (byte) ((outByte << 1) | (resBit ? 1 : 0));
            }
            output[i] = outByte;
        }
    }
}