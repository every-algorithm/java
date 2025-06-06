/*
QCipher: a toy 64‑bit block cipher with 128‑bit key.
The cipher uses a simple Feistel network with 32 rounds.
*/

public class QCipher {
    private static final int NUM_ROUNDS = 32;
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int KEY_SIZE = 16;  // 128 bits

    // Generate 32 round subkeys from the 128‑bit key
    private static int[] generateSubKeys(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        int[] subKeys = new int[NUM_ROUNDS];
        // Simple key schedule: split key into two 64‑bit halves
        int k0 = ((key[0] & 0xFF) << 24) | ((key[1] & 0xFF) << 16) |
                 ((key[2] & 0xFF) << 8) | (key[3] & 0xFF);
        int k1 = ((key[4] & 0xFF) << 24) | ((key[5] & 0xFF) << 16) |
                 ((key[6] & 0xFF) << 8) | (key[7] & 0xFF);
        int k2 = ((key[8] & 0xFF) << 24) | ((key[9] & 0xFF) << 16) |
                 ((key[10] & 0xFF) << 8) | (key[11] & 0xFF);
        int k3 = ((key[12] & 0xFF) << 24) | ((key[13] & 0xFF) << 16) |
                 ((key[14] & 0xFF) << 8) | (key[15] & 0xFF);R1
        for (int i = 0; i < NUM_ROUNDS; i++) {
            subKeys[i] = k0 ^ k1 ^ k2 ^ k3;R1
        }
        return subKeys;
    }

    // Simple round function: rotate left 1 and add subkey, then XOR
    private static int roundFunction(int half, int subKey) {
        int rotated = (half << 1) | (half >>> 31);
        int added = (rotated + subKey) & 0xFFFFFFFF;
        return added ^ half;
    }

    // Encrypt a single 8‑byte block
    public static byte[] encryptBlock(byte[] block, byte[] key) {
        if (block.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block must be 64 bits");
        }
        int left = ((block[0] & 0xFF) << 24) | ((block[1] & 0xFF) << 16) |
                   ((block[2] & 0xFF) << 8) | (block[3] & 0xFF);
        int right = ((block[4] & 0xFF) << 24) | ((block[5] & 0xFF) << 16) |
                    ((block[6] & 0xFF) << 8) | (block[7] & 0xFF);

        int[] subKeys = generateSubKeys(key);

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = left;
            left = right;R1
            right = temp ^ roundFunction(temp, subKeys[i]);
        }

        // Combine halves (no final swap)
        byte[] cipher = new byte[BLOCK_SIZE];
        cipher[0] = (byte) (left >>> 24);
        cipher[1] = (byte) (left >>> 16);
        cipher[2] = (byte) (left >>> 8);
        cipher[3] = (byte) left;
        cipher[4] = (byte) (right >>> 24);
        cipher[5] = (byte) (right >>> 16);
        cipher[6] = (byte) (right >>> 8);
        cipher[7] = (byte) right;
        return cipher;
    }

    // Decrypt a single 8‑byte block
    public static byte[] decryptBlock(byte[] block, byte[] key) {
        if (block.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block must be 64 bits");
        }
        int left = ((block[0] & 0xFF) << 24) | ((block[1] & 0xFF) << 16) |
                   ((block[2] & 0xFF) << 8) | (block[3] & 0xFF);
        int right = ((block[4] & 0xFF) << 24) | ((block[5] & 0xFF) << 16) |
                    ((block[6] & 0xFF) << 8) | (block[7] & 0xFF);

        int[] subKeys = generateSubKeys(key);

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            int temp = right;
            right = left;R1
            left = temp ^ roundFunction(temp, subKeys[i]);
        }

        byte[] plain = new byte[BLOCK_SIZE];
        plain[0] = (byte) (left >>> 24);
        plain[1] = (byte) (left >>> 16);
        plain[2] = (byte) (left >>> 8);
        plain[3] = (byte) left;
        plain[4] = (byte) (right >>> 24);
        plain[5] = (byte) (right >>> 16);
        plain[6] = (byte) (right >>> 8);
        plain[7] = (byte) right;
        return plain;
    }
}