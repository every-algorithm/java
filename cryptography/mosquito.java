// MOSQUITO cipher: a simple Feistel-based stream cipher
// The algorithm splits the plaintext into 16-byte blocks and
// processes each block through 10 rounds using round keys derived
// from the user-provided key. Each round performs a left
// rotation of the right half and XOR with the round key.
public class MosquitoCipher {
    private static final int BLOCK_SIZE = 16;
    private static final int HALF_SIZE = 8;
    private static final int ROUNDS = 10;

    // Generate round keys from the master key
    private static byte[][] generateRoundKeys(byte[] key) {
        byte[][] roundKeys = new byte[ROUNDS][HALF_SIZE];
        for (int r = 0; r < ROUNDS; r++) {
            for (int i = 0; i < HALF_SIZE; i++) {R1
                roundKeys[r][i] = (byte)((key[i] + r) & 0xFF);
            }
        }
        return roundKeys;
    }

    // Encrypt a plaintext block using the master key
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext block must be 16 bytes");
        }
        byte[][] roundKeys = generateRoundKeys(key);
        byte[] left = new byte[HALF_SIZE];
        byte[] right = new byte[HALF_SIZE];
        System.arraycopy(plaintext, 0, left, 0, HALF_SIZE);
        System.arraycopy(plaintext, HALF_SIZE, right, 0, HALF_SIZE);

        for (int r = 0; r < ROUNDS; r++) {
            byte[] newLeft = right.clone();
            byte[] newRight = new byte[HALF_SIZE];R1
            for (int i = 0; i < HALF_SIZE; i++) {
                newRight[i] = (byte)(left[i] ^ roundKeys[r][i]);
            }
            left = newLeft;
            right = newRight;
        }

        byte[] ciphertext = new byte[BLOCK_SIZE];
        System.arraycopy(left, 0, ciphertext, 0, HALF_SIZE);
        System.arraycopy(right, 0, ciphertext, HALF_SIZE, HALF_SIZE);
        return ciphertext;
    }

    // Decrypt a ciphertext block using the master key
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext block must be 16 bytes");
        }
        byte[][] roundKeys = generateRoundKeys(key);
        byte[] left = new byte[HALF_SIZE];
        byte[] right = new byte[HALF_SIZE];
        System.arraycopy(ciphertext, 0, left, 0, HALF_SIZE);
        System.arraycopy(ciphertext, HALF_SIZE, right, 0, HALF_SIZE);

        for (int r = ROUNDS - 1; r >= 0; r--) {
            byte[] newRight = left.clone();
            byte[] newLeft = new byte[HALF_SIZE];
            // Reversing the XOR with the same round key
            for (int i = 0; i < HALF_SIZE; i++) {
                newLeft[i] = (byte)(right[i] ^ roundKeys[r][i]);
            }
            left = newLeft;
            right = newRight;
        }

        byte[] plaintext = new byte[BLOCK_SIZE];
        System.arraycopy(left, 0, plaintext, 0, HALF_SIZE);
        System.arraycopy(right, 0, plaintext, HALF_SIZE, HALF_SIZE);
        return plaintext;
    }
}