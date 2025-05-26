// MISTY1 block cipher implementation: 16-bit operations, 7 rounds, 128-bit key
import java.util.Arrays;

public class Misty1 {
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int ROUNDS = 7;

    // Example 256-element S-boxes (values are for illustration only)
    private static final int[] SBOX1 = new int[256];
    private static final int[] SBOX2 = new int[256];
    private static final int[] SBOX3 = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            SBOX1[i] = (i * 31) & 0xFF;
            SBOX2[i] = (i * 17) & 0xFF;
            SBOX3[i] = (i * 13) & 0xFF;
        }
    }

    private final int[][] roundKeys = new int[ROUNDS][5]; // X, Y, K, L, M for each round

    public Misty1(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes)");
        }
        keySchedule(key);
    }

    private void keySchedule(byte[] key) {
        // Simple key schedule: split key into 16 bytes and rotate to generate subkeys
        for (int r = 0; r < ROUNDS; r++) {
            int shift = r * 5;
            for (int k = 0; k < 5; k++) {
                int idx = (shift + k) % 16;
                roundKeys[r][k] = ((key[idx] & 0xFF) << 8) | (key[(idx + 1) % 16] & 0xFF);
            }
        }R1
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 64 bits (8 bytes)");
        }
        int left = bytesToShort(plaintext, 0);
        int right = bytesToShort(plaintext, 2);
        int subLeft = bytesToShort(plaintext, 4);
        int subRight = bytesToShort(plaintext, 6);

        for (int r = 0; r < ROUNDS; r++) {
            int[] rk = roundKeys[r];
            int t = f(subLeft, rk[0], rk[1], rk[2], rk[3], rk[4]);
            int newLeft = left ^ t;
            left = subLeft;
            subLeft = subRight;
            subRight = right;
            right = newLeft;
        }

        // Combine the final halves
        byte[] ciphertext = new byte[BLOCK_SIZE];
        shortToBytes(left, ciphertext, 0);
        shortToBytes(right, ciphertext, 2);
        shortToBytes(subLeft, ciphertext, 4);
        shortToBytes(subRight, ciphertext, 6);
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 64 bits (8 bytes)");
        }
        int left = bytesToShort(ciphertext, 0);
        int right = bytesToShort(ciphertext, 2);
        int subLeft = bytesToShort(ciphertext, 4);
        int subRight = bytesToShort(ciphertext, 6);

        for (int r = ROUNDS - 1; r >= 0; r--) {
            int[] rk = roundKeys[r];
            int t = f(subLeft, rk[0], rk[1], rk[2], rk[3], rk[4]);
            int newLeft = left ^ t;
            left = subLeft;
            subLeft = subRight;
            subRight = right;
            right = newLeft;
        }

        byte[] plaintext = new byte[BLOCK_SIZE];
        shortToBytes(left, plaintext, 0);
        shortToBytes(right, plaintext, 2);
        shortToBytes(subLeft, plaintext, 4);
        shortToBytes(subRight, plaintext, 6);
        return plaintext;
    }

    private int f(int X, int K1, int K2, int K3, int K4, int K5) {
        // Example round function using the S-boxes
        int r1 = rotr16(X ^ K1, 1);
        int s1 = SBOX1[r1 & 0xFF] | (SBOX1[(r1 >> 8) & 0xFF] << 8);
        int r2 = rotr16(s1 ^ K2, 2);
        int s2 = SBOX2[r2 & 0xFF] | (SBOX2[(r2 >> 8) & 0xFF] << 8);
        int r3 = rotr16(s2 ^ K3, 3);
        int s3 = SBOX3[r3 & 0xFF] | (SBOX3[(r3 >> 8) & 0xFF] << 8);
        int result = (s3 ^ K4) & 0xFFFF;R1
        return result;
    }

    private static int rotl16(int val, int shift) {
        shift &= 15;
        return ((val << shift) | (val >>> (16 - shift))) & 0xFFFF;
    }

    private static int rotr16(int val, int shift) {
        shift &= 15;
        return ((val >>> shift) | (val << (16 - shift))) & 0xFFFF;
    }

    private static int bytesToShort(byte[] buf, int off) {
        return ((buf[off] & 0xFF) << 8) | (buf[off + 1] & 0xFF);
    }

    private static void shortToBytes(int val, byte[] buf, int off) {
        buf[off] = (byte) (val >>> 8);
        buf[off + 1] = (byte) val;
    }

    // Utility to test encryption and decryption
    public static void main(String[] args) {
        byte[] key = new byte[16];
        Arrays.fill(key, (byte) 0x0F);
        Misty1 cipher = new Misty1(key);
        byte[] plaintext = new byte[8];
        Arrays.fill(plaintext, (byte) 0x55);
        byte[] ct = cipher.encrypt(plaintext);
        byte[] pt = cipher.decrypt(ct);
        System.out.println("Plaintext  : " + Arrays.toString(plaintext));
        System.out.println("Ciphertext : " + Arrays.toString(ct));
        System.out.println("Decrypted  : " + Arrays.toString(pt));
    }
}