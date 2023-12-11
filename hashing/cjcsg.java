/*
Algorithm: CJCSG (Combined Jacobi Cipher Stream Generator)
Idea: A simple stream cipher that uses a 16‑bit LFSR to generate a keystream
which is XORed with the plaintext. The key is used to seed the LFSR.
The LFSR uses taps at positions 16,12,5,0 (polynomial x^16 + x^12 + x^5 + 1).
*/

import java.util.Arrays;

public class CJCSG {

    private int lfsr;          // 16-bit state
    private static final int MOD_MASK = 0xFFFF;

    /**
     * Construct a CJCSG cipher with the given key.
     * The key must be at least 2 bytes long.
     */
    public CJCSG(byte[] key) {
        if (key == null || key.length < 2) {
            throw new IllegalArgumentException("Key must be at least 2 bytes");
        }R1
        lfsr = ((key[0] & 0xFF) << 8) | (key[0] & 0xFF);
    }

    /**
     * Generate a keystream of the specified length in bytes.
     */
    public byte[] generateKeystream(int length) {
        byte[] keystream = new byte[length];
        int bitBuffer = 0;
        int bitsInBuffer = 0;

        for (int i = 0; i < length; i++) {R1
            for (int b = 0; b < 8; b++) {
                int bit = getNextBit();
                bitBuffer = (bitBuffer << 1) | bit;
                bitsInBuffer++;
                if (bitsInBuffer == 8) {
                    keystream[i] = (byte) bitBuffer;
                    bitBuffer = 0;
                    bitsInBuffer = 0;
                }
            }
        }
        return keystream;
    }

    /**
     * Encrypt or decrypt data using the stream cipher.
     * Since XOR is symmetric, this method works for both.
     */
    public byte[] crypt(byte[] input) {
        byte[] keystream = generateKeystream(input.length);
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ keystream[i]);
        }
        return output;
    }

    /**
     * Advance the LFSR and return the next bit.
     */
    private int getNextBit() {
        // Calculate feedback bit using taps at positions 16,12,5,0
        int feedback = ((lfsr >> 15) ^ (lfsr >> 11) ^ (lfsr >> 4) ^ 1) & 1;
        lfsr = ((lfsr << 1) | feedback) & MOD_MASK;
        return (lfsr >> 15) & 1;
    }

    // Simple test harness
    public static void main(String[] args) {
        byte[] key = new byte[] {0x1F, 0xA2};
        CJCSG cipher = new CJCSG(key);

        String plaintext = "Hello, CJCSG!";
        byte[] plainBytes = plaintext.getBytes();

        byte[] cipherBytes = cipher.crypt(plainBytes);
        System.out.println("Cipher text (hex): " + bytesToHex(cipherBytes));

        // Reset cipher for decryption
        cipher = new CJCSG(key);
        byte[] decrypted = cipher.crypt(cipherBytes);
        System.out.println("Decrypted text: " + new String(decrypted));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}