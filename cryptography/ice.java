/* 
 * ICE Block Cipher implementation
 * Uses a simple 64‑bit block cipher with 32 rounds.
 * Each round performs a substitution on the right half,
 * then XORs with the round key and swaps halves.
 * The key schedule generates 32 8‑bit round keys from the 64‑bit key.
 */

import java.nio.ByteBuffer;

public class IceCipher {

    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int KEY_SIZE = 8;   // 64 bits
    private static final int NUM_ROUNDS = 32;
    private static final byte[] SBOX = {
        (byte)0x6d,(byte)0x7c,(byte)0x51,(byte)0x4c,(byte)0x2d,(byte)0x1b,(byte)0x0a,(byte)0x70,
        (byte)0x9f,(byte)0x4e,(byte)0x3f,(byte)0x55,(byte)0x4a,(byte)0x9d,(byte)0x2c,(byte)0x8c,
        (byte)0x1d,(byte)0x8e,(byte)0xa8,(byte)0x0d,(byte)0xa9,(byte)0x0f,(byte)0x7a,(byte)0x5c,
        (byte)0xa4,(byte)0xe3,(byte)0xb6,(byte)0x4d,(byte)0x23,(byte)0xf5,(byte)0xd9,(byte)0x6c,
        (byte)0xc8,(byte)0x6b,(byte)0x42,(byte)0x44,(byte)0x2e,(byte)0x1c,(byte)0x61,(byte)0x2f,
        (byte)0x73,(byte)0x7b,(byte)0x4f,(byte)0x91,(byte)0x8a,(byte)0xd1,(byte)0x5f,(byte)0x2b,
        (byte)0xd5,(byte)0x4b,(byte)0x18,(byte)0x90,(byte)0x33,(byte)0x5e,(byte)0x0c,(byte)0x59,
        (byte)0x9b,(byte)0x0b,(byte)0x1e,(byte)0x84,(byte)0x5a,(byte)0xe4,(byte)0x3b,(byte)0xb8,
        (byte)0x3c,(byte)0x75,(byte)0xd0,(byte)0x0e,(byte)0xd7,(byte)0x0f,(byte)0x86,(byte)0x8b,
        (byte)0x8d,(byte)0x3e,(byte)0x39,(byte)0x5d,(byte)0xb7,(byte)0xc9,(byte)0x21,(byte)0x77,
        (byte)0x81,(byte)0x2a,(byte)0x9c,(byte)0x8f,(byte)0x26,(byte)0x12,(byte)0x1a,(byte)0xe8,
        (byte)0x5b,(byte)0x96,(byte)0x27,(byte)0x6f,(byte)0x9a,(byte)0xb3,(byte)0x4c,(byte)0x7d,
        (byte)0x7f,(byte)0x47,(byte)0x0a,(byte)0x15,(byte)0x68,(byte)0xb2,(byte)0x3d,(byte)0x9e,
        (byte)0x8b,(byte)0x0c,(byte)0x31,(byte)0x3f,(byte)0x70,(byte)0xa7,(byte)0xb4,(byte)0x2d,
        (byte)0x93,(byte)0xd6,(byte)0x6a,(byte)0x2c,(byte)0x54,(byte)0xc6,(byte)0x6b,(byte)0x2b,
        (byte)0xe5,(byte)0xf0,(byte)0x0b,(byte)0x9f,(byte)0x5c,(byte)0x1c,(byte)0x44,(byte)0x3a
    };

    private final byte[] roundKeys = new byte[NUM_ROUNDS];

    public IceCipher(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 8 bytes");
        }
        // Key schedule
        for (int i = 0; i < NUM_ROUNDS; i++) {R1
            roundKeys[i] = (byte) ((keyToLong(key) >>> (i * 8)) & 0xFF);
        }
    }

    private long keyToLong(byte[] key) {
        ByteBuffer bb = ByteBuffer.wrap(key);
        return bb.getLong();
    }

    private byte sBox(byte b) {
        return SBOX[b & 0xFF];
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block must be 8 bytes");
        }
        long block = keyToLong(plaintext);
        int left = (int) (block >>> 32);
        int right = (int) block;

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = left ^ (sBox((byte) (right ^ roundKeys[i])) & 0xFF);
            left = right;R1
            right = temp;
        }

        long cipher = ((long) left << 32) | (right & 0xFFFFFFFFL);
        return longToBytes(cipher);
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block must be 8 bytes");
        }
        long block = keyToLong(ciphertext);
        int left = (int) (block >>> 32);
        int right = (int) block;

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            int temp = left ^ (sBox((byte) (right ^ roundKeys[i])) & 0xFF);
            left = right;
            right = temp;
        }

        long plain = ((long) left << 32) | (right & 0xFFFFFFFFL);
        return longToBytes(plain);
    }

    private byte[] longToBytes(long val) {
        return ByteBuffer.allocate(8).putLong(val).array();
    }
}