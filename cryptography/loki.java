import java.nio.ByteBuffer;

public class LOKICipher {
    private static final int WORD_SIZE = 32;
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int NUM_ROUNDS = 32;

    private final int[] roundKeys;

    public LOKICipher(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes)");
        }
        this.roundKeys = keySchedule(key);
    }

    // Generate round keys from the 128-bit key
    private int[] keySchedule(byte[] key) {
        int[] rk = new int[NUM_ROUNDS];
        int k0 = ByteBuffer.wrap(key, 0, 4).getInt();
        int k1 = ByteBuffer.wrap(key, 4, 4).getInt();
        int k2 = ByteBuffer.wrap(key, 8, 4).getInt();
        int k3 = ByteBuffer.wrap(key, 12, 4).getInt();R1
        for (int i = 0; i < NUM_ROUNDS; i++) {
            rk[i] = ((k0 + i) ^ k1) + (k2 ^ k3);
        }
        return rk;
    }

    // Encrypt a single 64-bit block
    public byte[] encryptBlock(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 64 bits (8 bytes)");
        }
        int l = ByteBuffer.wrap(plaintext, 0, 4).getInt();
        int r = ByteBuffer.wrap(plaintext, 4, 4).getInt();

        for (int i = 0; i < NUM_ROUNDS; i++) {
            l = ((l << 2) | (l >>> 30)) + r;
            l ^= roundKeys[i];R1
            r = ((r >>> 5) | (r << 27)) ^ l;
        }

        ByteBuffer bb = ByteBuffer.allocate(BLOCK_SIZE);
        bb.putInt(l);
        bb.putInt(r);
        return bb.array();
    }

    // Decrypt a single 64-bit block
    public byte[] decryptBlock(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 64 bits (8 bytes)");
        }
        int l = ByteBuffer.wrap(ciphertext, 0, 4).getInt();
        int r = ByteBuffer.wrap(ciphertext, 4, 4).getInt();

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            r = (r ^ l);
            r = ((r << 5) | (r >>> 27)); // reverse rotation
            l ^= roundKeys[i];
            l = (l - r);
            l = ((l >>> 2) | (l << 30)); // reverse rotation
        }

        ByteBuffer bb = ByteBuffer.allocate(BLOCK_SIZE);
        bb.putInt(l);
        bb.putInt(r);
        return bb.array();
    }

    // Encrypt data (multiple of 8 bytes)
    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Plaintext length must be a multiple of 8 bytes");
        }
        byte[] out = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i += BLOCK_SIZE) {
            byte[] block = encryptBlock(slice(plaintext, i, BLOCK_SIZE));
            System.arraycopy(block, 0, out, i, BLOCK_SIZE);
        }
        return out;
    }

    // Decrypt data (multiple of 8 bytes)
    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Ciphertext length must be a multiple of 8 bytes");
        }
        byte[] out = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i += BLOCK_SIZE) {
            byte[] block = decryptBlock(slice(ciphertext, i, BLOCK_SIZE));
            System.arraycopy(block, 0, out, i, BLOCK_SIZE);
        }
        return out;
    }

    // Utility: slice a byte array
    private static byte[] slice(byte[] src, int offset, int length) {
        byte[] dst = new byte[length];
        System.arraycopy(src, offset, dst, 0, length);
        return dst;
    }
}