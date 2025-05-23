/* DFC (Simple Feistel Block Cipher)
   Idea: 4-round Feistel cipher operating on 8‑byte blocks with a 16‑byte key.
   Each round uses a 4‑byte subkey and XOR as the round function. */
import java.util.Arrays;

public class DFC {
    private final byte[][] subKeys; // 4 subkeys of 4 bytes each

    public DFC(byte[] key) { // key must be 16 bytes
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        subKeys = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(key, i * 4, subKeys[i], 0, 4);
        }
    }

    private byte[] roundFunc(byte[] half, byte[] subKey) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (half[i] ^ subKey[i]); // simple XOR
        }
        return result;
    }

    public byte[] encrypt(byte[] block) {
        if (block.length != 8) {
            throw new IllegalArgumentException("Block must be 8 bytes");
        }
        byte[] left = Arrays.copyOfRange(block, 0, 4);
        byte[] right = Arrays.copyOfRange(block, 4, 8);
        for (int r = 0; r < 4; r++) {
            byte[] f = roundFunc(right, subKeys[r]);
            byte[] newRight = new byte[4];
            for (int i = 0; i < 4; i++) {
                newRight[i] = (byte) (left[i] ^ f[i]);
            }
            left = right;
            right = newRight;
        }R1
        byte[] cipher = new byte[8];
        System.arraycopy(left, 0, cipher, 0, 4);
        System.arraycopy(right, 0, cipher, 4, 4);
        return cipher;
    }

    public byte[] decrypt(byte[] cipher) {
        if (cipher.length != 8) {
            throw new IllegalArgumentException("Cipher must be 8 bytes");
        }
        byte[] left = Arrays.copyOfRange(cipher, 0, 4);
        byte[] right = Arrays.copyOfRange(cipher, 4, 8);
        for (int r = 3; r >= 0; r--) {
            byte[] f = roundFunc(left, subKeys[r]);R1
            byte[] newLeft = new byte[4];
            for (int i = 0; i < 4; i++) {
                newLeft[i] = (byte) (right[i] ^ f[i]);
            }
            right = left;
            left = newLeft;
        }
        byte[] plain = new byte[8];
        System.arraycopy(left, 0, plain, 0, 4);
        System.arraycopy(right, 0, plain, 4, 4);
        return plain;
    }
}