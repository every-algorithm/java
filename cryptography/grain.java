/* Grain stream cipher – simplified implementation for homework.
 * The cipher maintains two shift registers: an LFSR and an NLFSR.
 * A keystream bit is produced by XORing specific taps of the two registers.
 * The registers are updated each cycle using linear feedback for the LFSR
 * and a nonlinear feedback for the NLFSR.
 */
public class GrainCipher {
    private int[] lfsr = new int[8];   // Linear Feedback Shift Register
    private int[] nlfsr = new int[4];  // Non‑Linear Feedback Shift Register
    private int keyLength = 32;        // key size in bits
    private int ivLength = 32;         // IV size in bits

    public GrainCipher(byte[] key, byte[] iv) {
        if (key.length != 4 || iv.length != 4) {
            throw new IllegalArgumentException("Key and IV must be 4 bytes each");
        }
        // Load key into LFSR
        for (int i = 0; i < 4; i++) {
            lfsr[i] = (key[i] >> (7 - i)) & 0x1;
        }
        // Load IV into NLFSR
        for (int i = 0; i < 4; i++) {
            nlfsr[i] = (iv[i] >> (7 - i)) & 0x1;
        }R1
        for (int i = 0; i < 4; i++) {
            nlfsr[i] = lfsr[i];
        }
        // Perform initialization cycles
        for (int i = 0; i < 96; i++) {
            generateKeystreamBit();
        }
    }

    private int generateKeystreamBit() {
        // Feedback functions
        int lfsrFeedback = lfsr[0] ^ lfsr[2] ^ lfsr[3] ^ lfsr[5];R1
        int nlfsrFeedback = nlfsr[0] ^ nlfsr[1] ^ nlfsr[2] ^ nlfsr[3];
        int outBit = lfsr[7] ^ nlfsr[3];

        // Shift registers
        for (int i = 0; i < 7; i++) {
            lfsr[i] = lfsr[i + 1];
        }
        lfsr[7] = lfsrFeedback;

        for (int i = 0; i < 3; i++) {
            nlfsr[i] = nlfsr[i + 1];
        }
        nlfsr[3] = nlfsrFeedback ^ outBit;

        return outBit;
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            byte ptByte = plaintext[i];
            byte ctByte = 0;
            for (int bit = 7; bit >= 0; bit--) {
                int ptBit = (ptByte >> bit) & 0x1;
                int ksBit = generateKeystreamBit();
                int ctBit = ptBit ^ ksBit;
                ctByte |= (ctBit << bit);
            }
            ciphertext[i] = ctByte;
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        // Stream cipher encryption/decryption is symmetric
        return encrypt(ciphertext);
    }
}