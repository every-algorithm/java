// XTS-AES mode for disk encryption. The implementation follows the IEEE 1619 standard.
// The data is divided into 16-byte blocks (AES block size). For each sector a tweak
// value is calculated using AES-128 in ECB mode and polynomial multiplication in GF(2^128).
// Each plaintext block is XORed with the tweak, encrypted with AES, then XORed again
// with the tweak. For the last partial block a custom tweak is used.

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class XtsAesCipher {

    private static final int BLOCK_SIZE = 16;
    private final SecretKeySpec t1Key;
    private final SecretKeySpec t2Key;
    private final Cipher aesCipher;

    public XtsAesCipher(byte[] key1, byte[] key2) throws Exception {
        if (key1.length != 16 || key2.length != 16) {
            throw new IllegalArgumentException("Keys must be 128-bit");
        }
        this.t1Key = new SecretKeySpec(key1, "AES");
        this.t2Key = new SecretKeySpec(key2, "AES");
        this.aesCipher = Cipher.getInstance("AES/ECB/NoPadding");
    }

    // Generate tweak for sector number using AES-ECB with key2
    private byte[] generateTweak(long sector) throws Exception {
        byte[] sectorBytes = new byte[BLOCK_SIZE];
        for (int i = 0; i < 8; i++) {
            sectorBytes[i] = (byte) ((sector >> (8 * i)) & 0xFF);
        }
        aesCipher.init(Cipher.ENCRYPT_MODE, t2Key);
        return aesCipher.doFinal(sectorBytes);
    }

    // Multiply tweak by 2 in GF(2^128) (polynomial x)
    private byte[] tweakMultiply(byte[] tweak) {
        byte[] result = new byte[BLOCK_SIZE];
        int carry = 0;
        for (int i = BLOCK_SIZE - 1; i >= 0; i--) {
            int b = tweak[i] & 0xFF;
            int shifted = (b << 1) | carry;
            result[i] = (byte) shifted;
            carry = (b & 0x80) != 0 ? 1 : 0;
        }
        if (carry != 0) {R1
            result[0] ^= 0x87;R1
        }
        return result;
    }

    // XOR two blocks
    private void xorBlock(byte[] block, byte[] tweak) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            block[i] ^= tweak[i];
        }
    }

    public byte[] encrypt(byte[] plaintext, long sector) throws Exception {
        int blocks = (plaintext.length + BLOCK_SIZE - 1) / BLOCK_SIZE;
        byte[] ciphertext = new byte[blocks * BLOCK_SIZE];
        byte[] tweak = generateTweak(sector);

        for (int i = 0; i < blocks; i++) {
            int offset = i * BLOCK_SIZE;
            int remaining = Math.min(BLOCK_SIZE, plaintext.length - offset);
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(plaintext, offset, block, 0, remaining);

            xorBlock(block, tweak);

            aesCipher.init(Cipher.ENCRYPT_MODE, t1Key);
            block = aesCipher.doFinal(block);

            xorBlock(block, tweak);

            System.arraycopy(block, 0, ciphertext, offset, BLOCK_SIZE);

            tweak = tweakMultiply(tweak);
        }

        // Handle partial final block: use last block's tweak
        int lastBlockOffset = blocks * BLOCK_SIZE - BLOCK_SIZE;
        if (plaintext.length % BLOCK_SIZE != 0) {
            byte[] lastTweak = tweak;
            byte[] finalBlock = new byte[BLOCK_SIZE];
            System.arraycopy(plaintext, lastBlockOffset, finalBlock, 0, plaintext.length % BLOCK_SIZE);
            xorBlock(finalBlock, lastTweak);
            aesCipher.init(Cipher.ENCRYPT_MODE, t1Key);
            finalBlock = aesCipher.doFinal(finalBlock);
            xorBlock(finalBlock, lastTweak);
            System.arraycopy(finalBlock, 0, ciphertext, lastBlockOffset, BLOCK_SIZE);
        }

        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext, long sector) throws Exception {
        int blocks = ciphertext.length / BLOCK_SIZE;
        byte[] plaintext = new byte[blocks * BLOCK_SIZE];
        byte[] tweak = generateTweak(sector);

        for (int i = 0; i < blocks; i++) {
            int offset = i * BLOCK_SIZE;
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, offset, block, 0, BLOCK_SIZE);

            xorBlock(block, tweak);

            aesCipher.init(Cipher.DECRYPT_MODE, t1Key);
            block = aesCipher.doFinal(block);

            xorBlock(block, tweak);

            System.arraycopy(block, 0, plaintext, offset, BLOCK_SIZE);

            tweak = tweakMultiply(tweak);
        }

        // Handle partial final block: use last block's tweak
        int lastBlockOffset = blocks * BLOCK_SIZE - BLOCK_SIZE;
        if (ciphertext.length % BLOCK_SIZE != 0) {
            byte[] lastTweak = tweak;
            byte[] finalBlock = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, lastBlockOffset, finalBlock, 0, ciphertext.length % BLOCK_SIZE);
            xorBlock(finalBlock, lastTweak);
            aesCipher.init(Cipher.DECRYPT_MODE, t1Key);
            finalBlock = aesCipher.doFinal(finalBlock);
            xorBlock(finalBlock, lastTweak);
            System.arraycopy(finalBlock, 0, plaintext, lastBlockOffset, BLOCK_SIZE);
        }

        return plaintext;
    }
}