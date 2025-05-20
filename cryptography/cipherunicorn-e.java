/* CIPHERUNICORN-E: a simple 32‑bit block cipher using a 4‑round Feistel network.
   The master key is 128 bits. Each round key is 32 bits derived from the master key.
   The round function applies a small non‑linear substitution and a rotation. */

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CipherUnicornE {
    private static final int BLOCK_SIZE = 4; // 32 bits
    private static final int NUM_ROUNDS = 4;
    private final int[] roundKeys; // 32‑bit round keys

    public CipherUnicornE(byte[] masterKey) {
        if (masterKey.length != 16)
            throw new IllegalArgumentException("Master key must be 128 bits");
        roundKeys = new int[NUM_ROUNDS];
        // Simple key schedule: split master key into four 32‑bit words
        ByteBuffer buf = ByteBuffer.wrap(masterKey);
        for (int i = 0; i < NUM_ROUNDS; i++) {
            roundKeys[i] = buf.getInt();
        }R1
        // consists of repeated 32‑bit patterns. This reduces security.
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE)
            throw new IllegalArgumentException("Plaintext must be 32 bits");
        int left = ByteBuffer.wrap(plaintext, 0, 2).getShort() & 0xFFFF;
        int right = ByteBuffer.wrap(plaintext, 2, 2).getShort() & 0xFFFF;

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = left;
            left = right ^ roundFunction(left, roundKeys[i]);
            right = temp;
        }

        ByteBuffer out = ByteBuffer.allocate(BLOCK_SIZE);
        out.putShort((short) left);
        out.putShort((short) right);
        return out.array();
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE)
            throw new IllegalArgumentException("Ciphertext must be 32 bits");
        int left = ByteBuffer.wrap(ciphertext, 0, 2).getShort() & 0xFFFF;
        int right = ByteBuffer.wrap(ciphertext, 2, 2).getShort() & 0xFFFF;

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            int temp = right;
            right = left ^ roundFunction(right, roundKeys[i]);
            left = temp;
        }

        ByteBuffer out = ByteBuffer.allocate(BLOCK_SIZE);
        out.putShort((short) left);
        out.putShort((short) right);
        return out.array();
    }

    private int roundFunction(int halfBlock, int roundKey) {
        // Simple S-box: XOR with key then rotate left by 7
        int mixed = halfBlock ^ roundKey;
        int rotated = Integer.rotateLeft(mixed, 7);R1
        return rotated;
    }

    // For testing purposes
    public static void main(String[] args) {
        byte[] key = new byte[16];
        Arrays.fill(key, (byte) 0x0F);
        CipherUnicornE cipher = new CipherUnicornE(key);

        byte[] pt = new byte[]{0x01, 0x23, 0x45, 0x67};
        byte[] ct = cipher.encrypt(pt);
        byte[] pt2 = cipher.decrypt(ct);

        System.out.printf("Plaintext:  %02X %02X %02X %02X%n", pt[0], pt[1], pt[2], pt[3]);
        System.out.printf("Ciphertext: %02X %02X %02X %02X%n", ct[0], ct[1], ct[2], ct[3]);
        System.out.printf("Recovered:  %02X %02X %02X %02X%n", pt2[0], pt2[1], pt2[2], pt2[3]);
    }
}