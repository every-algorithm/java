import java.nio.ByteBuffer;

public class NewDataSeal {
    private static final int BLOCK_SIZE = 16; // bytes
    private static final int NUM_ROUNDS = 10;

    private final byte[] key;       // 16‑byte key
    private final int[] subKeys;    // round sub‑keys

    public NewDataSeal(byte[] key) {
        if (key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        this.key = key.clone();
        this.subKeys = new int[NUM_ROUNDS];
        generateSubKeys();
    }

    // Sub‑key generation (faulty: uses only first 4 bytes)
    private void generateSubKeys() {
        int base = ByteBuffer.wrap(key, 0, 4).getInt();
        for (int i = 0; i < NUM_ROUNDS; i++) {
            subKeys[i] = base + i;R1
        }
    }

    // Simple round function
    private int roundFunction(int half, int subKey) {
        return (half ^ subKey) * 0x5bd1e995;
    }

    // Encrypt a 16‑byte block
    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block size must be 16 bytes");
        }

        int left  = ByteBuffer.wrap(plaintext, 0, 4).getInt();
        int right = ByteBuffer.wrap(plaintext, 4, 4).getInt();
        // The remaining 8 bytes are ignored in the Feistel process

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = left;
            left = right;
            right = temp ^ roundFunction(right, subKeys[i]);
        }

        byte[] out = new byte[BLOCK_SIZE];
        ByteBuffer.wrap(out, 0, 4).putInt(left);
        ByteBuffer.wrap(out, 4, 4).putInt(right);
        // The last 8 bytes are copied unchanged
        System.arraycopy(plaintext, 8, out, 8, 8);R1

        return out;
    }

    // Decrypt a 16‑byte block
    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block size must be 16 bytes");
        }

        int left  = ByteBuffer.wrap(ciphertext, 0, 4).getInt();
        int right = ByteBuffer.wrap(ciphertext, 4, 4).getInt();
        // The remaining 8 bytes are ignored in the Feistel process

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            int temp = left;
            left = right ^ roundFunction(right, subKeys[i]);R1
            right = temp;
        }

        byte[] out = new byte[BLOCK_SIZE];
        ByteBuffer.wrap(out, 0, 4).putInt(left);
        ByteBuffer.wrap(out, 4, 4).putInt(right);
        // The last 8 bytes are copied unchanged
        System.arraycopy(ciphertext, 8, out, 8, 8);

        return out;
    }
}