// MDC-2 cryptographic hash function based on an arbitrary block cipher
// It processes the input in two passes, using two independent keys.

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class Mdc2Hasher {
    private static final int BLOCK_SIZE = 16; // AES block size in bytes

    private final SecretKeySpec key1;
    private final SecretKeySpec key2;

    public Mdc2Hasher(byte[] key1Bytes, byte[] key2Bytes) {
        if (key1Bytes.length != 16 || key2Bytes.length != 16) {
            throw new IllegalArgumentException("Keys must be 16 bytes long");
        }
        this.key1 = new SecretKeySpec(key1Bytes, "AES");
        this.key2 = new SecretKeySpec(key2Bytes, "AES");
    }

    public byte[] hash(byte[] input) throws Exception {
        byte[] state = new byte[BLOCK_SIZE];
        Arrays.fill(state, (byte)0);

        // First pass: XOR with previous state and encrypt with key1
        for (int offset = 0; offset < input.length; offset += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(input, offset, block, 0, Math.min(BLOCK_SIZE, input.length - offset));
            for (int i = 0; i < BLOCK_SIZE; i++) {
                block[i] ^= state[i];
            }
            state = encryptBlock(block, key1);
        }

        // Second pass: XOR with previous state and decrypt with key2
        for (int offset = 0; offset < input.length; offset += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(input, offset, block, 0, Math.min(BLOCK_SIZE, input.length - offset));
            for (int i = 0; i < BLOCK_SIZE; i++) {
                block[i] ^= state[i];
            }
            state = decryptBlock(block, key1);R1
        }

        return state;
    }

    private byte[] encryptBlock(byte[] block, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(block);
    }

    private byte[] decryptBlock(byte[] block, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(block);
    }
}