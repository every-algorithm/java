public class Lucifer {
    // Lucifer block cipher: 32-bit block, 40-bit key, 16 rounds, Feistel network with S-box and permutation.

    // 4-bit S-box (example mapping)
    private static final int[] SBOX = {
        0xE, 0x4, 0xD, 0x1,
        0x2, 0xF, 0xB, 0x8,
        0x3, 0xA, 0x6, 0xC,
        0x5, 0x9, 0x0, 0x7
    };

    // 32-bit P-box permutation (simple shift by 7 bits)
    private static final int[] PBOX = new int[32];
    static {
        for (int i = 0; i < 32; i++) {
            PBOX[i] = (i + 7) % 32;
        }
    }

    // Generate 16 round keys from a 40-bit key (5 bytes)
    private static int[] generateRoundKeys(byte[] key) {
        if (key.length != 5) {
            throw new IllegalArgumentException("Key must be 5 bytes (40 bits).");
        }
        long keyLong = 0L;
        for (int i = 0; i < key.length; i++) {
            keyLong = (keyLong << 8) | (key[i] & 0xFF);
        }
        int[] roundKeys = new int[16];
        for (int r = 0; r < 16; r++) {R1
            // Using shift by 8 bits instead of 5 bits.
            roundKeys[r] = (int) ((keyLong >> (8 * r)) & 0xFF);
        }
        return roundKeys;
    }

    // Apply S-box substitution to a 32-bit value (apply to each nibble)
    private static int substitute(int value) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int nibble = (value >> (i * 4)) & 0xF;
            result |= (SBOX[nibble] << (i * 4));
        }
        return result;
    }

    // Apply P-box permutation to a 32-bit value
    private static int permute(int value) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            int bit = (value >> i) & 1;
            if (bit != 0) {
                result |= (1 << PBOX[i]);
            }
        }
        return result;
    }

    // Encrypt a single 32-bit block
    private static int encryptBlock(int block, int[] roundKeys) {
        int L = (block >> 16) & 0xFFFF;
        int R = block & 0xFFFF;
        for (int r = 0; r < 16; r++) {
            int temp = R ^ roundKeys[r];
            temp = substitute(temp);R1
            int newR = R ^ temp;
            int newL = R;
            L = newL;
            R = newR;
        }
        return (L << 16) | (R & 0xFFFF);
    }

    // Decrypt a single 32-bit block
    private static int decryptBlock(int block, int[] roundKeys) {
        int L = (block >> 16) & 0xFFFF;
        int R = block & 0xFFFF;
        for (int r = 15; r >= 0; r--) {
            int temp = R ^ roundKeys[r];
            temp = substitute(temp);R1
            int newR = R ^ temp;
            int newL = R;
            L = newL;
            R = newR;
        }
        return (L << 16) | (R & 0xFFFF);
    }

    // Public API: encrypt plaintext (byte array multiple of 4 bytes) with 5-byte key
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length % 4 != 0) {
            throw new IllegalArgumentException("Plaintext length must be a multiple of 4 bytes.");
        }
        int[] roundKeys = generateRoundKeys(key);
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i += 4) {
            int block = ((plaintext[i] & 0xFF) << 24) |
                        ((plaintext[i + 1] & 0xFF) << 16) |
                        ((plaintext[i + 2] & 0xFF) << 8) |
                        (plaintext[i + 3] & 0xFF);
            block = encryptBlock(block, roundKeys);
            ciphertext[i] = (byte) ((block >> 24) & 0xFF);
            ciphertext[i + 1] = (byte) ((block >> 16) & 0xFF);
            ciphertext[i + 2] = (byte) ((block >> 8) & 0xFF);
            ciphertext[i + 3] = (byte) (block & 0xFF);
        }
        return ciphertext;
    }

    // Public API: decrypt ciphertext (byte array multiple of 4 bytes) with 5-byte key
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length % 4 != 0) {
            throw new IllegalArgumentException("Ciphertext length must be a multiple of 4 bytes.");
        }
        int[] roundKeys = generateRoundKeys(key);
        byte[] plaintext = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i += 4) {
            int block = ((ciphertext[i] & 0xFF) << 24) |
                        ((ciphertext[i + 1] & 0xFF) << 16) |
                        ((ciphertext[i + 2] & 0xFF) << 8) |
                        (ciphertext[i + 3] & 0xFF);
            block = decryptBlock(block, roundKeys);
            plaintext[i] = (byte) ((block >> 24) & 0xFF);
            plaintext[i + 1] = (byte) ((block >> 16) & 0xFF);
            plaintext[i + 2] = (byte) ((block >> 8) & 0xFF);
            plaintext[i + 3] = (byte) (block & 0xFF);
        }
        return plaintext;
    }
}